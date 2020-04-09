package com;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.nio.file.Path;

public class SerachIndex {

    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    @Before
    public void init() throws Exception {
        indexReader = DirectoryReader.open(FSDirectory.open(new File("D:\\360MoveData\\Users\\Administrator\\Desktop\\常用\\java-test\\target").toPath()));
        indexSearcher = new IndexSearcher(indexReader);
    }

    @Test
    public void testRangeQuery() throws Exception {
        //创建一个Query对象
        Query query = LongPoint.newRangeQuery("size", 0l, 100l);
        printResult(query);
    }

    private void printResult(Query query) throws Exception{
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总记录条数："+topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc doc : scoreDocs) {
            int docId = doc.doc;
            Document document = indexSearcher.doc(docId);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
            System.out.println(document.get("content"));
        }
        indexReader.close();
    }


    @Test
    public void testQueryParser() throws Exception {
        // 创建一个QueryParser对象，两个参数：参数1：默认搜索域 参数2：分析器对象
        QueryParser queryParser = new QueryParser("name", new IKAnalyzer());

        //使用QueryParser对象创建一个Query对象
        Query query = queryParser.parse("lucene是一个Java开发的全文检索工具包");

        //执行查询
        printResult(query);
    }


}
