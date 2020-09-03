//package com.tigerobo.search.parser;
//
//import com.tigerobo.search.ElasticSearchApplication;
//import com.tigerobo.search.entity.Element;
//import com.tigerobo.search.entity.MetaDataStatement;
//import org.junit.Test;
//import org.junit.Before;
//import org.junit.After;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.junit4.SpringRunner;
//
//
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//
///**
// * ReadHelper Tester.
// *
// * @author <Authors name>
// * @version 1.0
// * @since <pre>Jun 19, 2020</pre>
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = ElasticSearchApplication.class)
//public class ReadHelperTest {
//
//
//    private MetaDataStatement metaDataStatement;
//    private ReadHelper readHelper = new ReadHelper(metaDataStatement);
//
//    @Before
//    public void before() throws Exception {
//    }
//
//    @After
//    public void after() throws Exception {
//    }
//
//    /**
//     * Method: getElement(String sql)
//     */
//    @Test
//    public void testGetElement() throws Exception {
//
//        String sql = "select name , age,sex,school from index where (name = '1234' and (sex='1' or school='ceshi'))";
//        metaDataStatement = new MetaDataStatement(sql);
//        metaDataStatement.setSql(sql);
//        List<Element> els = readHelper.getElement();
//
////TODO: Test goes here...
//    }
//
//    /**
//     * Method: isNeedMerge(Element pre)
//     */
//    @Test
//    public void testIsNeedMerge() throws Exception {
////TODO: Test goes here...
//    }
//
//    /**
//     * Method: getSymbolOfStr(List<String> symbol, String value)
//     */
//    @Test
//    public void testGetSymbolOfStr() throws Exception {
////TODO: Test goes here...
//    }
//
//    /**
//     * Method: splitStr(String sql)
//     */
//    @Test
//    public void testSplitStr() throws Exception {
////TODO: Test goes here...
//    }
//
//    /**
//     * Method: getString(Integer start, String[] values, List<Integer> endIndex)
//     */
//    @Test
//    public void testGetString() throws Exception {
////TODO: Test goes here...
//    }
//
//    /**
//     * Method: getStringByToken(String prefix, String suffix, String[] values, Integer start, List<String> res, List<Integer> endIndex, Boolean isStart)
//     */
//    @Test
//    public void testGetStringByToken() throws Exception {
////TODO: Test goes here...
//    }
//
//
//}
