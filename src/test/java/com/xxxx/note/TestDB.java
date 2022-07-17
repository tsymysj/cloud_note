package com.xxxx.note;

import com.xxxx.note.dao.NoteDao;
import com.xxxx.note.po.Note;
import com.xxxx.note.util.DBUtil;
import com.xxxx.note.vo.NoteVo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestDB {

    //使用日志工厂类，记录日志
    private Logger logger = LoggerFactory.getLogger(TestDB.class);

    /**
     * 单元测试方法
     *  1.方法的返回值，建议使用void，一般没有返回值
     *  2.参数列表，建议空参，一般是没参数
     *  3.方法上需要设置@Test注解
     *  4.每个方法都能独立运行
     */
    @Test
    public void testDB(){
        System.out.println(DBUtil.getConnetion());
        //使用日志
        logger.info("获取数据库连接: "+DBUtil.getConnetion());
        logger.info("获取数据库连接: {}",DBUtil.getConnetion());
    }
    @Test
    public void testDBQuery(){
        NoteDao noteDao = new NoteDao();
//        List<Note> list = noteDao.findNoteListByPage(1,1,5,null);
//
//        for (int i = 0; i < list.size(); i++) {
//            System.out.print(list.get(i).getNoteId()+" ");
//            System.out.print(list.get(i).getTitle()+" ");
//            System.out.print(list.get(i).getPubTime()+" ");
//            System.out.print(list.get(i).getTypeId()+" ");
//            System.out.println();
//        }

    }

    @Test
    public void testDBTime(){
        NoteDao noteDao = new NoteDao();
        List<NoteVo> list = noteDao.findNoteCountByDate(1);
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i).getGroupName()+" ");
            System.out.print(list.get(i).getNoteCount()+" ");
            System.out.println();
        }
    }
}
