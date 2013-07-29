package egovframework.rte.psl.data.jpa.repository;

import org.springframework.data.repository.CrudRepository;

import egovframework.rte.psl.data.jpa.domain.Article;

public interface ArticleRepository extends CrudRepository<Article, Long> {
	
}
