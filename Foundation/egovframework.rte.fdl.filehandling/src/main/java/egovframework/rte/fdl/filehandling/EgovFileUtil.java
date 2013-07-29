/*
 * Copyright 2008-2009 MOPAS(Ministry of Public Administration and Security).
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.regexp.RE;

import egovframework.rte.fdl.string.EgovStringUtil;

/**
 * 파일 서비스을 제공하는 유틸 클래스
 * <p>
 * <b>NOTE:</b> 파일 서비스를 제공하기 위해 구현한 클래스이다.
 * @author 실행환경 개발팀 윤성종
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  윤성종           최초 생성
 * 
 * </pre>
 */
public class EgovFileUtil {

    /**
     * <p>
     * 에러나 이벤트와 관련된 각종 메시지를 로깅하기 위한 Log 오브젝트
     * </p>
     */

    private static final Log log = LogFactory.getLog(EgovFileUtil.class);

    private static FileObject basefile;
    private static FileSystemManager manager;

    static {
        try {
            manager = VFS.getManager();
            basefile = manager.resolveFile(System.getProperty("user.dir"));
        } catch (FileSystemException e) {
            log.error("EgovFileUtil : " + e.getMessage());
        }
    }

    /**
     * <p>
     * 지정한 위치의 파일 및 디렉토리를 삭제한다.
     * </p>
     * @param cmd
     *        <code>String</code>
     * @return 결과 값
     * @throws FileSystemException
     */
    public static int rm(final String cmd) throws FileSystemException {
        int result = -1;

        try {
            final FileObject file = manager.resolveFile(basefile, cmd);

            result = file.delete(Selectors.SELECT_SELF_AND_CHILDREN);

            log.debug("result is " + result);

        } catch (FileSystemException e) {
            log.error(e.toString());
            throw new FileSystemException(e);
        }

        return result;
    }

    /**
     * <p>
     * 지정한 위치의 파일을 대상 위치로 복사한다.
     * </p>
     * @param source
     *        <code>String</code>
     * @param target
     *        <code>String</code>
     * @throws Exception
     */
    public static void cp(String source, String target) throws Exception {

        try {
            final FileObject src = manager.resolveFile(basefile, source);
            FileObject dest = manager.resolveFile(basefile, target);

            if (dest.exists() && dest.getType() == FileType.FOLDER) {
                dest = dest.resolveFile(src.getName().getBaseName());
            }

            dest.copyFrom(src, Selectors.SELECT_ALL);
        } catch (FileSystemException fse) {
            log.error(fse.toString());
            ;
            throw new FileSystemException(fse);
        }
    }

    /**
     * <p>
     * 지정한 위치의 파일을 대상 위치로 이동한다.
     * </p>
     * @param source
     *        <code>String</code>
     * @param target
     *        <code>String</code>
     * @throws Exception
     */
    public static void mv(String source, String target) throws Exception {

        try {
            final FileObject src = manager.resolveFile(basefile, source);
            FileObject dest = manager.resolveFile(basefile, target);

            if (dest.exists() && dest.getType() == FileType.FOLDER) {
                dest = dest.resolveFile(src.getName().getBaseName());
            }

            src.moveTo(dest);
        } catch (FileSystemException fse) {
            log.error(fse.toString());
            ;
            throw new FileSystemException(fse);
        }
    }

    /**
     * <p>
     * 현재 작업위치를 리턴한다.
     * </p>
     * @return 현재 위치
     */
    public static FileName pwd() {
        return basefile.getName();
    }

    /**
     * <p>
     * 파일의 일시를 현재 일시로 변경한다.
     * </p>
     * @param filepath
     *        <code>String</code>
     * @return
     * @throws Exception
     */
    public static long touch(final String filepath) throws Exception {

        long currentTime = 0;
        final FileObject file = manager.resolveFile(basefile, filepath);

        if (!file.exists()) {
            file.createFile();
        }

        file.getContent().setLastModifiedTime(
            currentTime = System.currentTimeMillis());

        return currentTime;
    }

    /**
     * <p>
     * 현재 작업공간의 위치를 지정한 위치로 이동한다.
     * </p>
     * @param changDirectory
     *        <code>String</code>
     * @throws Exception
     */
    public static void cd(final String changDirectory) throws Exception {
        final String path;
        if (!EgovStringUtil.isNull(changDirectory)) {
            path = changDirectory;
        } else {
            path = System.getProperty("user.home");
        }

        // Locate and validate the folder
        FileObject tmp = manager.resolveFile(basefile, path);

        if (tmp.exists()) {
            basefile = tmp;
        } else {
            log.info("Folder does not exist: " + tmp.getName());
        }
        log.info("Current folder is " + basefile.getName());
    }

    /**
     * <p>
     * 지정한 위치의 파일목록을 조회한다.
     * </p>
     * @param cmd
     *        <code>String[]</code>
     * @return 조회된 파일 목록
     * @throws FileSystemException
     */
    public List ls(final String[] cmd) throws FileSystemException {
        List list = new ArrayList();

        int pos = 1;
        final boolean recursive;
        if (cmd.length > pos && cmd[pos].equals("-R")) {
            recursive = true;
            pos++;
        } else {
            recursive = false;
        }

        final FileObject file;
        if (cmd.length > pos) {
            file = manager.resolveFile(basefile, cmd[pos]);
        } else {
            file = basefile;
        }

        if (file.getType() == FileType.FOLDER) {
            // List the contents
            log.info("Contents of " + file.getName());
            log.info(listChildren(file, recursive, ""));
            // list.add(file.getName());
        } else {
            // Stat the file
            log.info(file.getName());
            final FileContent content = file.getContent();
            log.info("Size: " + content.getSize() + " bytes.");
            final DateFormat dateFormat =
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                    DateFormat.MEDIUM);
            final String lastMod =
                dateFormat.format(new Date(content.getLastModifiedTime()));
            log.info("Last modified: " + lastMod);
        }

        return list;
    }

    /**
     * <p>
     * 지정한 위치의 하위 디렉토리 목록을 가져온다.
     * </p>
     * @param dir
     *        <code>FileObject</code>
     * @param recursive
     *        <code>boolean</code>
     * @param prefix
     *        <code>String</code>
     * @return 디렉토리 목록
     * @throws FileSystemException
     */
    private StringBuffer listChildren(final FileObject dir,
            final boolean recursive, final String prefix)
            throws FileSystemException {
        StringBuffer line = new StringBuffer();
        final FileObject[] children = dir.getChildren();

        for (int i = 0; i < children.length; i++) {
            final FileObject child = children[i];
            // log.info(prefix +
            // child.getName().getBaseName());
            line.append(prefix).append(child.getName().getBaseName());

            if (child.getType() == FileType.FOLDER) {
                // log.info("/");
                line.append("/");
                if (recursive) {
                    line
                        .append(listChildren(child, recursive, prefix + "    "));
                }
            } else {
                line.append("");
            }
        }

        return line;
    }

    /**
     * <p>
     * 파일을 읽는다.
     * </p>
     * @param file
     *        <code>File</code>
     * @return 결과 값
     * @throws IOException
     */
    public static String readFile(File file) throws IOException {
        BufferedInputStream in =
            new BufferedInputStream(new FileInputStream(file));
        return readFileContent(in);
    }

    /**
     * <p>
     * String 형으로 파일의 내용을 읽는다.
     * </p>
     * @param the
     *        file input stream.
     *        <code>InputStrea</code>
     * @return 파일 내용
     * @throws IOException
     */
    public static String readFileContent(InputStream in) throws IOException {
        StringBuffer buf = new StringBuffer();

        for (int i = in.read(); i != -1; i = in.read()) {
            buf.append((char) i);
        }

        return buf.toString();
    }

    /**
     * <p>
     * String 영으로 파일의 내용을 읽는다.
     * </p>
     * @param file
     *        <code>File</code>
     * @param encoding
     *        <code>String</code>
     * @return 파일 내용
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static String readFile(File file, String encoding)
            throws IOException {
        StringBuffer sb = new StringBuffer();

        List<String> lines = FileUtils.readLines(file, encoding);

        for (Iterator<String> it = lines.iterator();;) {
            sb.append(it.next());

            if (it.hasNext()) {
                sb.append("");
            } else {
                break;
            }
        }

        return sb.toString();
    }

    /**
     * <p>
     * 텍스트 내용을 파일로 쓴다.
     * </p>
     * @param file
     *        <code>File</code>
     * @param text
     *        <code>String</code>
     */
    public static void writeFile(File file, String text) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(file);
            writer.write(text);
        } catch (Exception e) {
            log.error("Error creating File: " + file.getName() + ":" + e);
            return;
        } finally {
            try {
                writer.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * <p>
     * 텍스트 내용을 파일로 쓴다.
     * </p>
     * @param fileName
     *        <code>String</code>
     * @param text
     *        <code>String</code>
     */
    public static void writeFile(String fileName, String text) {
        writeFile(new File(fileName), text);
    }

    public static void writeFile(String fileName, String data, String encoding)
            throws IOException {
        FileUtils.writeStringToFile(new File(fileName), data, encoding);
    }

    /*
     * Saves the content to the file. 한글 저장시 오류...점검
     * public static void saveFile(String filename,
     * String content) throws IOException {
     * FileOutputStream fos = null;
     * BufferedOutputStream bos = null; try { fos = new
     * FileOutputStream(filename); bos = new
     * BufferedOutputStream(fos);
     * bos.write(content.getBytes(), 0,
     * content.length()); } finally { if (bos != null)
     * bos.close(); if (fos != null) fos.close(); } }
     */

    /**
     * <p>
     * byte형으로 파일의 내용을 읽어온다.
     * <p>
     * @param file
     *        <code>FileObject</code>
     * @return 파일 내용
     * @throws IOException
     */
    public static byte[] getContent(final FileObject file) throws IOException {
        final FileContent content = file.getContent();
        final int size = (int) content.getSize();
        final byte[] buf = new byte[size];

        final InputStream in = content.getInputStream();
        try {
            int read = 0;
            for (int pos = 0; pos < size && read >= 0; pos += read) {
                read = in.read(buf, pos, size - pos);
            }
        } finally {
            in.close();
        }

        return buf;
    }

    /**
     * <p>
     * 내용을 파일에 OutputStream 으로 저장한다.
     * </p>
     * @param file
     * @param outstr
     * @throws IOException
     */
    public static void writeContent(final FileObject file,
            final OutputStream outstr) throws IOException {
        final InputStream instr = file.getContent().getInputStream();
        try {
            final byte[] buffer = new byte[1024];
            while (true) {
                final int nread = instr.read(buffer);
                if (nread < 0) {
                    break;
                }
                outstr.write(buffer, 0, nread);
            }
        } finally {
            instr.close();
        }
    }

    // Create the output stream via getContent(),
    // to pick up the validation it does
    /**
     * <p>
     * 파일 객체를 대상 파일객체로 복사한다.
     * </p>
     * @param srcFile
     *        <code>FileObject</code>
     * @param destFile
     *        <code>FileObject</code>
     * @throws IOException
     */
    public static void copyContent(final FileObject srcFile,
            final FileObject destFile) throws IOException {
        final OutputStream outstr = destFile.getContent().getOutputStream();
        try {
            writeContent(srcFile, outstr);
        } finally {
            outstr.close();
        }
    }

    /**
     * <p>
     * 파일의 확장자를 가져온다.
     * </p>
     * @param filename
     *        <code>String</code>
     * @return 파일확장자
     */
    public static String getFileExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    /**
     * <p>
     * 파일의 존재여부를 확인한다.
     * </p>
     * @param filename
     *        <code>String</code>
     * @return 존재여부
     */
    public static boolean isExistsFile(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    /**
     * <p>
     * 디렉토리명을 제외한 파일명을 가져온다.
     * </p>
     * @param filename
     *        <code>String</code>
     * @return
     */
    public static String stripFilename(String filename) {
        return FilenameUtils.getBaseName(filename);
    }

    /**
     * <p>
     * 저정한 파일을 삭제한다.
     * </p>
     * @param file
     *        <code>File</code>
     * @throws IOException
     */
    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            // it's a folder, list children first
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                delete(children[i]);
            }
        }
        if (!file.delete()) {
            throw new IOException("Unable to delete " + file.getPath());
        }
    }

    /**
     * <p>
     * 텍스트 파일을 읽어온다.
     * </p>
     * @param fileName
     *        <code>String</code>
     * @param newline
     *        <code>boolean</code>
     * @return 파일 내용
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static StringBuffer readTextFile(String fileName, boolean newline)
            throws FileNotFoundException, IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        StringBuffer buf = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
                if (newline) {
                    buf.append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            // log.error(e, module);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // log.error(e, module);
                }
            }
        }

        return buf;
    }

    /**
     * <p>
     * 특정위치의 파일 객체를 가져온다.
     * </p>
     * @param filepath
     *        <code>String</code>
     * @return 파일 객체
     * @throws Exception
     */
    public static FileObject getFileObject(final String filepath)
            throws Exception {
        FileSystemManager mgr = VFS.getManager();

        return mgr.resolveFile(mgr.resolveFile(System.getProperty("user.dir")),
            filepath);
    }

    /**
     * <p>
     * 특정 패턴이 존재하는 파일을 검색한다.
     * </p>
     * @param search
     *        <code>Object[]</code>
     * @param pattern
     *        <code>String</code>
     * @return 파일 목록
     * @throws Exception
     */
    public static List<String> grep(final Object[] search, final String pattern)
            throws Exception {
        RE searchPattern = new RE(pattern);

        String[] strings = searchPattern.grep(search);
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < strings.length; i++) {
            list.add(strings[i]);
        }

        return list;
    }

    /**
     * <p>
     * 특정 패턴이 존재하는 파일을 검색한다.
     * </p>
     * @param file
     *        <code>File</code>
     * @param pattern
     *        <code>String</code>
     * @return 파일 목록
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static List<String> grep(final File file, final String pattern)
            throws Exception {
        RE searchPattern = new RE(pattern);

        List<String> lists = FileUtils.readLines(file);
        Object[] search = lists.toArray();

        String[] strings = searchPattern.grep(search);
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < strings.length; i++) {
            list.add(strings[i]);
        }

        return list;
    }

}
