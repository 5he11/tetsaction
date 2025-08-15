//package com.frenzy.core.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.ruoyi.core.domain.PageData;
//import com.ruoyi.core.domain.YfForm;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.List;
//
///**
// * <p>
// *  服务类
// * </p>
// *
// * @author yf
// * @since 2021-12-12
// */
//public interface IYfFormService extends IService<YfForm> {
//
//    //根据表名查询
//    public YfForm findByFormName(String formName);
//
//    //根据条件查询查询
//    public List<YfForm> listByFiter(YfForm form);
//
//
//    //单条执行sql
//    public List<PageData> getSqlList(String sql);
//
//    //单条执行sql
//    public PageData getSqlMode(String sql);
//
//    //单条执行sql
//    public String getSqlString(String sql);
//
//    //单条执行sql
//    public void runSql(String sql);
//
//    //储存表单
//    public YfForm saveFrom(YfForm form);
//
//    //复制表单
//    public void copyFrom(YfForm form);
//
//    //删除表单记录和移除数据库表单
//    public void dropFormByFormID(int formId,String formName);
//    //删除表单
//    public void removeFrom(String[] formId);
//
//    //修改数据表注释
//    public void editFormComment(String formName, String formTitle);
//
//    /**
//     * 导出表单代码
//     * @param formId
//     * @return
//     */
//    void proCode(String formId , HttpServletResponse response) throws Exception;
//
//    byte[] export(List<Long> formIds);
//
//    void importForm(String json);
//}
