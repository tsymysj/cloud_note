package com.xxxx.note.web;

import com.alibaba.fastjson.JSON;
import com.xxxx.note.po.NoteType;
import com.xxxx.note.po.User;
import com.xxxx.note.service.NoteTypeService;
import com.xxxx.note.util.JsonUtil;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet {

    private NoteTypeService typeService = new NoteTypeService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //设置首页导航的高亮值
        req.setAttribute("menu_page","type");

        String actionName = req.getParameter("actionName");
        if ("list".equals(actionName)){
            //查询类型列表
            typeList(req,resp);
        }else if ("delete".equals(actionName)){
            //删除类型
            deleteType(req,resp);
        }else if ("addOrUpdate".equals(actionName)){
            //添加/修改类型操作
            addOrUpdate(req,resp);
        }
    }

    /**
     * 添加/修改类型操作
     * @param req
     * @param resp
     */
    private void addOrUpdate(HttpServletRequest req, HttpServletResponse resp) {
        String typeName = req.getParameter("typeName");
        String typeId = req.getParameter("typeId");
        User user = (User) req.getSession().getAttribute("user");
        ResultInfo<Integer> resultInfo = typeService.addOrUpdate(typeName,user.getUserId(),typeId);

        JsonUtil.toJson(resp,resultInfo);

    }

    /**
     * 删除类型
     * @param req
     * @param resp
     */
    private void deleteType(HttpServletRequest req, HttpServletResponse resp) {
        String typeId = req.getParameter("typeId");
        ResultInfo<NoteType> resultInfo = typeService.deleteType(typeId);
        //转化成JSON的字符串
        JsonUtil.toJson(resp,resultInfo);
    }

    /**
     * 查询类型列表
     * @param req
     * @param resp
     */
    private void typeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        List<NoteType> typeList = typeService.findTypeList(user.getUserId());
        req.setAttribute("typeList",typeList);
        req.setAttribute("changePage","type/list.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
