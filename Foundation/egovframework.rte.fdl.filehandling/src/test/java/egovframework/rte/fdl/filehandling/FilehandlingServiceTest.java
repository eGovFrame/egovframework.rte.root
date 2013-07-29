/*
 * Copyright 2002-2008 MOPAS(Ministry of Public Administration and Security).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.rte.fdl.filehandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.CacheStrategy;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FilesCache;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * FileServiceTest is TestCase of File Handling Service
 * @author Seongjong Yoon
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:spring/context-*.xml" })
public class FilehandlingServiceTest {

	private static final Log log = LogFactory.getLog(FilehandlingServiceTest.class);

	String filename = "";
	String text = "";
	String tmppath = "";

    @Before
    public void onSetUp() throws Exception {
    	filename = "test.txt";
    	text = "test입니다.";
    	tmppath = "tmp";
    }
	
    /**
     * 특정 위치에 파일을 생성하고 필요에 따라 생성한 파일을 캐싱한다.
     * @throws Exception
     */
    @Test
    public void testCeateFile() throws Exception {
    	FileSystemManager manager = VFS.getManager();
    	
    	FileObject baseDir = manager.resolveFile(System.getProperty("user.dir"));
    	final FileObject file = manager.resolveFile(baseDir, "testfolder/file1.txt");
    	
    	// 모든 파일 삭제
    	file.delete(Selectors.SELECT_FILES);
    	assertFalse(file.exists());

    	// 파일 생성
    	file.createFile();    	
    	assertTrue(file.exists());
    }
    
    /**
     * 특정 위치에 존재하는 파일에 접근하여 파일 내용을 수정한다.
     * 파일 위치는 절대 경로 또는 상대 경로 등 다양한 형식을 지원한다.
     * @throws Exception
     */
    @Test
    public void testAccessFile() throws Exception {

    	FileSystemManager manager = VFS.getManager();
    	
    	FileObject baseDir = manager.resolveFile(System.getProperty("user.dir"));
    	FileObject file = manager.resolveFile(baseDir, "testfolder/file1.txt");
    	
    	// 모든 파일 삭제
    	file.delete(Selectors.SELECT_FILES);
    	assertFalse(file.exists());

    	// 파일 생성
    	file.createFile();    	
    	assertTrue(file.exists());

    	FileContent fileContent = file.getContent();
    	assertEquals(0, fileContent.getSize());

    	// 파일 쓰기
    	String string = "test입니다.";
    	OutputStream os = fileContent.getOutputStream();

    	try {
	    	os.write(string.getBytes());
	    	os.flush();
    	} catch (Exception e) {
    		throw new Exception(e);
    	} finally {
    		os.close();
    	}
    	assertNotSame(0, fileContent.getSize());

    	// 파일 읽기
    	StringBuffer sb = new StringBuffer();
    	FileObject writtenFile = manager.resolveFile(baseDir, "testfolder/file1.txt");
    	FileContent writtenContents = writtenFile.getContent();
    	InputStream is = writtenContents.getInputStream();

    	try {
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	
	    	for (String line = ""; (line = reader.readLine()) != null; sb.append(line));
	    	
    	} catch (Exception e) {
    		throw new Exception(e);
    	} finally {
    		is.close();
    	}

    	// 파일내용 검증
    	assertEquals(sb.toString(), string);
    }
    
    /**
     * 캐싱 기능을 사용하여, 생성하거나 수정할 파일을 메모리 상에 로딩함으로써
     * 파일 접근 시에 소요되는 시간을 단축한다.
     * @throws Exception
     */
    @Test
    public void testCaching() throws Exception {
    	String testFolder = "d:/workspace/java/e-gov/eGovFramework/RTE/DEV/trunk/Foundation/egovframework.rte.fdl.filehandling/testfolder";
    	FileSystemManager manager = VFS.getManager();
    	
    	FileObject scratchFolder = manager.resolveFile(testFolder);

    	// testfolder 내의 모든 파일 삭제
        scratchFolder.delete(Selectors.EXCLUDE_SELF);

        // 캐싱 Manager 생성
        DefaultFileSystemManager fs = new DefaultFileSystemManager();
	    fs.setFilesCache(manager.getFilesCache());

	    // zip, jar, tgz, tar, tbz2, file
	    if (!fs.hasProvider("file")) {
	        fs.addProvider("file", new DefaultLocalFileProvider());
	    }
	    
	    fs.setCacheStrategy(CacheStrategy.ON_RESOLVE);
        fs.init();

        // 캐싱 객체 생성
        FileObject foBase2 = fs.resolveFile(testFolder);
        log.debug("## scratchFolder.getName().getPath() : " + scratchFolder.getName().getPath());

        FileObject cachedFolder = foBase2.resolveFile(scratchFolder.getName().getPath());
        
        // 파일이 존재하지 않음
        FileObject[] fos = cachedFolder.getChildren();
        assertFalse(contains(fos, "file1.txt"));

        // 파일생성
        scratchFolder.resolveFile("file1.txt").createFile();

        // 파일 존재함
        // BUT cachedFolder 에는 파일이 존재하지 않음
        fos = cachedFolder.getChildren();
        assertFalse(contains(fos, "file1.txt"));

        // 새로고침
        cachedFolder.refresh();
        // 파일이 존재함
        fos = cachedFolder.getChildren();
        assertTrue(contains(fos, "file1.txt"));

    }

    /**
     * @throws Exception
     */    @Test
    public void testWriteFile() throws Exception {
        
    	// delete file
    	if (EgovFileUtil.isExistsFile(filename))
    		EgovFileUtil.delete(new File(filename));

    	EgovFileUtil.writeFile(filename, text, "UTF-8");
    	assertTrue(EgovFileUtil.isExistsFile(filename));
    }
	
	/**
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
    public void testReadFile() throws Exception {

    	if (!EgovFileUtil.isExistsFile(filename))
    		EgovFileUtil.writeFile(filename, text, "UTF-8");

    	assertEquals(EgovFileUtil.readFile(new File(filename), "UTF-8"), text);
    	
    	//log.debug(EgovFileUtil.readTextFile(filename, false));

    	List<String> lines = FileUtils.readLines(new File(filename), "UTF-8");
    	log.debug(lines.toString());
    	
    	String string = lines.get(0);
    	
    	assertEquals(text, string);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testCp() throws Exception {
        
    	if (!EgovFileUtil.isExistsFile(filename))
    		EgovFileUtil.writeFile(filename, text);

    	EgovFileUtil.cp(filename, tmppath + "/" + filename);

    	assertEquals(
    			EgovFileUtil.readFile(new File(filename), "UTF-8"),
    			EgovFileUtil.readFile(new File(tmppath + "/" + filename), "UTF-8")
    	);
    }
    
    
    /**
     * @throws Exception
     */
    @Test
    public void testMv() throws Exception {

    	if (!EgovFileUtil.isExistsFile(tmppath + "/" + filename))
    		EgovFileUtil.writeFile(tmppath + "/" + filename, text);

    	EgovFileUtil.mv(tmppath + "/" + filename, tmppath + "/movedfile.txt");

    	assertEquals(
    			EgovFileUtil.readFile(new File(filename), "UTF-8"),
    			EgovFileUtil.readFile(new File(tmppath + "/movedfile.txt"), "UTF-8")
    	);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void testTouch() throws Exception {

    	String path = tmppath + "/movedfile.txt";
    	FileObject file = EgovFileUtil.getFileObject(path);
    	
    	long setTime = EgovFileUtil.touch(path);

    	assertEquals(file.getContent().getLastModifiedTime(), setTime);

    }

    /**
     * @throws Exception
     */
    @Test
    public void testGetFileExtension() throws Exception {
        
    	assertTrue(EgovFileUtil.isExistsFile(filename));
    	assertEquals(EgovFileUtil.getFileExtension(filename), "txt");

    }
   
    /**
     * @throws Exception
     */
    @Test
    public void testIsExistsFile() throws Exception {
        
       	assertTrue(EgovFileUtil.isExistsFile(filename));

    }

    /**
     * @throws Exception
     */
    @Test
    public void testStripFilename() throws Exception {
        
    	assertTrue(EgovFileUtil.isExistsFile(filename));
    	assertEquals("test", EgovFileUtil.stripFilename(filename));

    }

    /**
     * @throws Exception
     */
    @Test
    public void testRm() throws Exception {
        
    	String tmptarget = tmppath;// + "/movedfile.txt";

    	if (!EgovFileUtil.isExistsFile(tmptarget))
    		EgovFileUtil.writeFile(tmptarget, text);

    	int result = EgovFileUtil.rm(tmptarget);

    	assertTrue(result > 0);
    	assertFalse(EgovFileUtil.isExistsFile(tmptarget));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testCd() throws Exception {
        
    	String path = "c:/windows";
    	FileName foldername = EgovFileUtil.getFileObject(path).getName();
    	
    	EgovFileUtil.cd("");
    	
    	String uri = EgovFileUtil.pwd().getURI();
    	log.debug("EgovFileUtil.pwd().getURI() : " + uri);
    	log.debug("foldername.getURI() : " + foldername.getURI());
    	
    	assertFalse(foldername.getURI().equals(uri));

    	/////////////////////////////////////////////////////////////////
    	// c:\windows 로 이동
    	EgovFileUtil.cd(path);

    	uri = EgovFileUtil.pwd().getURI();
    	log.debug("EgovFileUtil.pwd().getURI() : " + uri);
    	log.debug("foldername.getURI() : " + foldername.getURI());
    	assertEquals(foldername.getURI(), EgovFileUtil.pwd().getURI());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testIOUtils() throws Exception {
    	 InputStream in = new URL( "http://jakarta.apache.org" ).openStream();
    	 try {
    		 
    	     assertFalse( IOUtils.toString( in ).equals("") );
    	   
    	 } finally {
    	     IOUtils.closeQuietly(in);
    	 }
    }
    
    /**
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
	@Test
    public void testFileSystemUtils() throws Exception {
    	
    	try {
    		long freeSpace = FileSystemUtils.freeSpace("C:/");

    		assertTrue(freeSpace > 0);

    	} catch (Exception e) {
    		log.error(e.getCause());
    	}
    }

    /**
     * @throws Exception
     */
    @Test
    public void testGrep() throws Exception {
    	
    	try {
    		String[] search = {"abcdefg", "efghijklmn", "12", "3"};

    		List<String> lists = EgovFileUtil.grep(search, "\\d{1,2}");
    		
    		for (Iterator<String> it = lists.iterator(); it.hasNext();) {
    			log.info(it.next());
    		}
    		
    		
    		lists = EgovFileUtil.grep(new File("pom.xml"), "egovframework.rte");
    		
    		for (Iterator<String> it = lists.iterator(); it.hasNext();) {
    			log.info(it.next());
    		}


    	} catch (Exception e) {
    		log.error(e.getCause());
    	}
    }

    /**
     * @throws Exception
     */
    @Test
    public void testLineIterator() throws Exception {

    	String[] string = {
	    	"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
	    	"  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">",
	    	"  <parent>",
	    	"  	<groupId>egovframework.rte</groupId>",
	    	"  	<artifactId>egovframework.rte.root</artifactId>",
	    	"  	<version>1.0.0-SNAPSHOT</version>",
	    	"  </parent>",
	    	"  <modelVersion>4.0.0</modelVersion>",
	    	"  <groupId>egovframework.rte</groupId>",
	    	"  <artifactId>egovframework.rte.fdl.filehandling</artifactId>",
	    	"  <packaging>jar</packaging>",
	    	"  <version>1.0.0-SNAPSHOT</version>",
	    	"  <name>egovframework.rte.fdl.filehandling</name>",
	    	"  <url>http://maven.apache.org</url>",
	    	"  <dependencies>",
	    	"    <dependency>",
	    	"      <groupId>junit</groupId>",
	    	"      <artifactId>junit</artifactId>",
	    	"      <version>4.4</version>",
	    	"      <scope>test</scope>",
	    	"    </dependency>",
	    	"    <dependency>",
	    	"      <groupId>commons-vfs</groupId>",
	    	"      <artifactId>commons-vfs</artifactId>",
	    	"      <version>1.0</version>",
	    	"    </dependency>",
	    	"    <dependency>",
	    	"      <groupId>commons-io</groupId>",
	    	"      <artifactId>commons-io</artifactId>",
	    	"      <version>1.4</version>",
	    	"    </dependency>",
	    	"    <!-- egovframework.rte -->",
	    	"    <dependency>",
	    	"      <groupId>egovframework.rte</groupId>",
	    	"      <artifactId>egovframework.rte.fdl.string</artifactId>",
	    	"      <version>1.0.0-SNAPSHOT</version>",
	    	"    </dependency>",
	    	"  </dependencies>",
	    	"</project>"
    	};

    	
    	try {
	    	File file = new File("pom.xml");
	    	
	    	LineIterator it = FileUtils.lineIterator(file, "UTF-8");
	    	
	    	try {
	    		log.debug("############################# LineIterator ###############################");
	    		
	    	 	for (int i = 0; it.hasNext(); i++) {
	    	    	String line = it.nextLine();
	    	    	log.info(line);

	    	    	assertEquals(string[i], line);
	    	 	}
	    	 } finally {
	    	 	LineIterator.closeQuietly(it);
	    	 }
	    	
    	} catch (Exception e) {
    		log.error(e.getCause());
    	}
    }

    @Test
    public void testUserAuthentication() throws Exception {
    	StaticUserAuthenticator auth = new StaticUserAuthenticator(null, "username", "password");
    	FileSystemOptions opts = new FileSystemOptions();
    	DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
    	
    	FileSystemManager manager = VFS.getManager();

    	FileObject baseDir = manager.resolveFile(System.getProperty("user.dir"));
    	FileObject file = manager.resolveFile(baseDir, "testfolder/file1.txt");
    	FileObject fo = manager.resolveFile("d:" + file.getName().getPath(), opts);
    	
    	fo.createFile();
    	
    }
    
    @Test
    public void testCaching1() throws Exception {
    	String testFolder = "d:/workspace/java/e-gov/eGovFramework/RTE/DEV/trunk/Foundation/egovframework.rte.fdl.filehandling/test";
    	

    	FileSystemManager manager = VFS.getManager();

    	EgovFileUtil.writeFile(testFolder + "/file1.txt", text, "UTF-8");

	    /*
	     * 캐싱 Manager 생성
	     * CacheStrategy.MANUAL		: Deal with cached data manually. Call FileObject.refresh() to refresh the object data.
	     * CacheStrategy.ON_RESOLVE : Refresh the data every time you request a file from FileSystemManager.resolveFile
	     * CacheStrategy.ON_CALL	: Refresh the data every time you call a method on the fileObject. You'll use this only if you really need the latest info as this setting is a major performance loss. 
	     */    	
        DefaultFileSystemManager fs = new DefaultFileSystemManager();
	    fs.setFilesCache(manager.getFilesCache());

	    // zip, jar, tgz, tar, tbz2, file
	    if (!fs.hasProvider("file")) {
	        fs.addProvider("file", new DefaultLocalFileProvider());
	    }
//    	StandardFileSystemManager fs = new StandardFileSystemManager();
	    
	    fs.setCacheStrategy(CacheStrategy.ON_RESOLVE);
        fs.init();
        
        
        // 캐싱 객체 생성
        //FileObject foBase2 = fs.resolveFile(testFolder);
        log.debug("####1");
        FileObject cachedFile = fs.toFileObject(new File(testFolder + "/file1.txt"));
        log.debug("####2");
        
        FilesCache filesCache = fs.getFilesCache();
        log.debug("####3");
        filesCache.putFile(cachedFile);
        FileObject obj = filesCache.getFile(cachedFile.getFileSystem(), cachedFile.getName());

        //FileObject baseFile = fs.getBaseFile();
//        log.debug("### cachedFile.getContent().getSize() is " + cachedFile.getContent().getSize());

        
//        long fileSize = cachedFile.getContent().getSize();
//        log.debug("#########size is " + fileSize);
        //FileObject cachedFile1 = cachedFile.resolveFile("file2.txt");

//    	FileObject scratchFolder = manager.resolveFile(testFolder);
//    	scratchFolder.delete(Selectors.EXCLUDE_SELF);

        EgovFileUtil.delete(new File(testFolder + "/file1.txt"));

//    	obj.createFile();
        
//        log.debug("#########obj is " + obj.toString());
//        log.debug("#########size is " + obj.getContent().getSize());
        log.debug("#########file is " + obj.exists());

        fs.close();
    }
    
    @Test
    public void testCaching3() throws Exception {
    	FileSystemManager manager = VFS.getManager();
    	String testFolder = "d:/workspace/java/e-gov/eGovFramework/RTE/DEV/trunk/Foundation/egovframework.rte.fdl.filehandling/test";
    	FileObject scratchFolder = manager.resolveFile(testFolder);
    	
    	// releaseable
        FileObject dir1 = scratchFolder.resolveFile("file1.txt");

        // avoid cache removal
        FileObject dir2 = scratchFolder.resolveFile("file2.txt");
        dir2.getContent();

        // check if the cache still holds the right instance
        FileObject dir2_2 = scratchFolder.resolveFile("file2.txt");
        assertTrue(dir2 == dir2_2);

        // check if the cache still holds the right instance
       /* FileObject dir1_2 = scratchFolder.resolveFile("file1.txt");
        assertFalse(dir1 == dir1_2);*/
    }

	
	private boolean contains(FileObject[] fos, String string) {
		for (int i = 0; i<fos.length; i++) {
			if (string.equals(fos[i].getName().getBaseName())) {
				log.debug("# " + string);
				return true;
			}
		}
		
		log.debug("# " + string + " should be seen");
		return false;
	}
    /*
     * 보류
     * 
    @Test
    public void testLs() throws Exception {
        
        try {

        	String path = "/workspace/java";
        	String[] cmd = {""};
        	util.cd(path);

        	List list = util.ls(cmd);

        	for (Iterator it = list.iterator(); it.hasNext();) {
        		log.info((String)it.next());
        	}
        	//assertEquals(path, util.pwd().getPath());

        } catch (Exception e) {
        	
        }
    }
    
    */
/*
    @Test
    public void testDelete() throws Exception {
        
        try {

        	assertTrue(FileUtil.isExistsFile(filename));
        	FileUtil.delete(new File(filename));
        	assertFalse(FileUtil.isExistsFile(filename));

        } catch (Exception e) {
        	
        }
    }
*/

	private boolean equals(byte b[], String s) {
        if(b == null || s == null)
            return false;
        byte[] stringbyte = s.getBytes();
        
//        if(b.length != slen)
//            return false;
        for(int i = 0; i < stringbyte.length; i++)
            if(b[i] != stringbyte[i])
                return false;

        return true;
    }
}
