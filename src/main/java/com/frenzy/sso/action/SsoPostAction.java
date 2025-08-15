package com.frenzy.sso.action;

import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.sso.domain.SsoPost;
import com.frenzy.sso.service.SsoPostService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SsoPostAction {

    public static final String API_PREFIX = "sso/post";

    @Autowired
    private SsoPostService postService;



    @ApiOperation(value = "获取岗位列表")
    @FunctionName("ssoPostList")
    public Object ssoPostList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoPost>> request,
            final ExecutionContext context) {

        SsoPost post = new SsoPost();
        if (!request.getBody().isEmpty()){
            post = request.getBody().get();
        }

        List<SsoPost> list = postService.selectPostList(post);
        return AjaxResult.success(request, list);
    }


    @ApiOperation(value = "根据岗位编号获取详细信息")
    @FunctionName("ssoPostGetInfo")
    public Object ssoPostGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{postId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("postId") String postId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, postService.selectPostById(postId));
    }



    @ApiOperation(value = "新增岗位")
    @FunctionName("ssoPostAdd")
    public Object ssoPostAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoPost>> request,
            final ExecutionContext context) {

        SsoPost post = request.getBody().get();
        ValidationUtil.validate(post);

        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return AjaxResult.error(request, "新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return AjaxResult.error(request, "新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        postService.insertPost(post);
        return AjaxResult.success(request);
    }




    @ApiOperation(value = "修改岗位")
    @FunctionName("ssoPostEdit")
    public Object ssoPostEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoPost>> request,
            final ExecutionContext context) {

        SsoPost post = request.getBody().get();
        ValidationUtil.validate(post);

        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return AjaxResult.error(request, "修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return AjaxResult.error(request, "修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        postService.updatePost(post);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "删除岗位")
    @FunctionName("ssoPostRemove")
    public Object ssoPostRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{postIds}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("postIds") String[] postIds, // 绑定路径参数
            final ExecutionContext context) {
        postService.deletePostByIds(postIds);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "获取岗位选择框列表")
    @FunctionName("ssoPostOptionselect")
    public Object ssoPostOptionselect(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/optionselect", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        List<SsoPost> posts = postService.selectPostAll();
        return AjaxResult.success(request, posts);
    }


}
