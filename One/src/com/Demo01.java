package com;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class Demo01 {


    public static void main(String[] args) throws Exception {
//        chinese();
//
//        createIndex();
        search();
    }

    private static void search() throws Exception {
        //1 创建一个Directory对象，指定索引库的位置。
        Directory directory = FSDirectory.open(new File("D:\\360MoveData\\Users\\Administrator\\Desktop\\常用\\java-test\\target").toPath());
        //2 创建一个IndexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3 创建一个IndexSearcher对象，构造方法中传递IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4 创建一个Query对象，TermQuery
        TermQuery query = new TermQuery(new Term("name", "apache"));
        //5 执行查询
        //参数1 查询对象 参数2 查询结果返回的最大记录数
        TopDocs topDocs = indexSearcher.search(query, 10);
        //6 获取查询结果的总记录数
        System.out.println("查询总记录数："+topDocs.totalHits);
        //7 获取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //8 打印文档中的信息
        for (ScoreDoc doc : scoreDocs) {
            //取文档id
            int docId = doc.doc;
            //根据id取文档对象
            Document document = indexSearcher.doc(docId);
            System.out.println("文档名字："+document.get("name"));
            System.out.println("文档路径："+document.get("path"));
            System.out.println("文档内容："+"/n"+document.get("content"));
            System.out.println("文档大小："+document.get("size"));
            System.out.println("==========================");
        }
        //9 关闭IndexReader对象
        indexReader.close();

    }

    private static void createIndex() throws Exception {
        //1 创建一个Directory对象，指定索引库的位置
        //保存在内存中（一般不会用）
//        Directory directory = new RAMDirectory();
        Directory directory = FSDirectory.open(
                new File("D:\\360MoveData\\Users\\Administrator\\Desktop\\常用\\java-test\\target").toPath());

        //2 基于Directory对象创建一个IndexWriter对象

        //使用IKAnalyzer
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
//        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //3 读取磁盘上的文件，给对应的每个文件创建一个文档对象
        File dir = new File("D:\\360MoveData\\Users\\Administrator\\Desktop\\常用\\java-test\\searchsource");
        File[] files = dir.listFiles();

        // 遍历数组
        for (File f : files) {
            //获取文件名
            String fileName = f.getName();
            //获取文件路径
            String filePath = f.getPath();
            //获取文件内容
            String fileContent = FileUtils.readFileToString(f, "utf-8");
            //获取文件大小
            long fileSize = FileUtils.sizeOf(f);

            // 4创建Filed域对象，存储以上内容
            //改造
            Field fieldOfName = new TextField("name",fileName, Field.Store.YES);


//            Field fieldOfPath = new TextField("path",filePath,Field.Store.YES);
            Field fieldOfPath = new StoredField("path",filePath);
            Field fieldOfContent = new TextField("content",fileContent, Field.Store.YES);

//            Field fieldOfSize = new TextField("size",fileSize+"",Field.Store.YES);
            Field fieldOfSizeValue = new LongPoint("size",fileSize);
            Field fieldOfSizeStore = new StoredField("size",fileSize);
            // 创建文档对象
            Document document = new Document();

            //4 向文档对象中添加域
            document.add(fieldOfName);
            document.add(fieldOfPath);
            document.add(fieldOfContent);
//            document.add(fieldOfSize);
            document.add(fieldOfSizeValue);
            document.add(fieldOfSizeStore);
            //5 把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //6 关闭IndexWriter对象
        indexWriter.close();
    }

    private static void testTokenStream() throws Exception{
        //1 创建一个Analyzer对象，StandardAnalyzer对象
        Analyzer analyzer = new StandardAnalyzer();
        //2 使用分析器对象的tokenStream方法获得一个TokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("", "Learn how to create a web page with Spring MVC.");
        //3 向TokenStream对象中设置一个引用，相当于一个指针
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //4 调用TokenStream对象的reset方法。如果不调用会抛异常
        tokenStream.reset();
        //5 使用while循环遍历TokenStream对象
        while(tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        //6 关闭TokenStream对象
        tokenStream.close();
    }

    private static void chinese() throws Exception {
        //1 创建一个Analyzer对象，StandardAnalyzer对象
        Analyzer analyzer = new IKAnalyzer();
        //2 使用分析器对象的tokenStream方法获得一个TokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("", "lucene北京四合院设计有限公司公安局传智播客");
        //3 向TokenStream对象中设置一个引用，相当于一个指针
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //4 调用TokenStream对象的reset方法。如果不调用会抛异常
        tokenStream.reset();
        //5 使用while循环遍历TokenStream对象
        while(tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        //6 关闭TokenStream对象
        tokenStream.close();
    }



}
