package com.xxxx.note.web;

import cn.hutool.json.JSONUtil;
import com.xxxx.note.po.Note;
import com.xxxx.note.po.User;
import com.xxxx.note.service.NoteService;
import com.xxxx.note.util.JsonUtil;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

    private NoteService noteService = new NoteService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置导航高亮
        req.setAttribute("menu_page","report");

        //得到用户行为
        String actionName = req.getParameter("actionName");

        //判断用户行为
        if ("info".equals(actionName)){
            //进入页面
            reportInfo(req,resp);
        }else if ("month".equals(actionName)){
            //通过月份查询对应的云记数量
            queryNoteCountByMonth(req,resp);
        }else if ("location".equals(actionName)){
            //查询发布云记时的坐标
            queryNoteLonAndLat(req,resp);
        }

    }

    /**
     * 查询发布云记时的坐标
     * @param req
     * @param resp
     */
    private void queryNoteLonAndLat(HttpServletRequest req, HttpServletResponse resp) {
        //从session作用域中获取用户对象
        User user = (User) req.getSession().getAttribute("user");
        ResultInfo<List<Note>> resultInfo = noteService.queryNoteLonAndLat(user.getUserId());
        //将resultInfo对象转换成JSON的字符串
        JsonUtil.toJson(resp,resultInfo);
    }

    /**
     * 通过月份查询对应的云记数量
     * @param req
     * @param resp
     */
    private void queryNoteCountByMonth(HttpServletRequest req, HttpServletResponse resp) {
        //从session作用域中获取用户对象
        User user = (User) req.getSession().getAttribute("user");

        ResultInfo<Map<String,Object>> resultInfo = noteService.queryNoteCountByMonth(user.getUserId());
        //将resultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
        JsonUtil.toJson(resp,resultInfo);
    }

    /**
     * 进入报表页面
     * @param req
     * @param resp
     */
    private void reportInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //设置首页动态包含的页面之
        req.setAttribute("changePage","report/info.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);

    }
}
