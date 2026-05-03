package com.zifang.z.agent.core.yuque;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 语雀 API 客户端工具类（全功能版）
 * 包含：查询/创建/更新/覆盖/删除/移动文档、知识库管理等
 */
public class YuQueClient {
    private static final Logger log = LoggerFactory.getLogger(YuQueClient.class);
    private static final Gson GSON = new Gson();

    private final OkHttpClient client;
    private final String token;
    private final String authHeaderValue;

    /**
     * 构造函数
     * @param token 语雀 Access Token（需有读写权限）
     */
    public YuQueClient(String token) {
        this.token = token;
        this.authHeaderValue = "Bearer " + token;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(YuQueConstant.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(YuQueConstant.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(YuQueConstant.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    // ==================== 基础通用方法 ====================
    private <T> YuQueResponse<T> executeRequest(Request request, Class<T> clazz) {
        YuQueResponse<T> response = new YuQueResponse<>();
        try {
            Response httpResponse = client.newCall(request).execute();
            if (httpResponse.isSuccessful() && httpResponse.body() != null) {
                String body = httpResponse.body().string();
                T data = GSON.fromJson(body, clazz);
                response.setSuccess(true);
                response.setData(data);
            } else {
                String errorMsg = String.format("请求失败：%d %s", httpResponse.code(), httpResponse.message());
                response.setSuccess(false);
                response.setErrorMsg(errorMsg);
                log.error(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "请求异常：" + e.getMessage();
            response.setSuccess(false);
            response.setErrorMsg(errorMsg);
            log.error(errorMsg, e);
        }
        return response;
    }

    private RequestBody buildJsonRequestBody(Object param) {
        return RequestBody.create(GSON.toJson(param), MediaType.parse("application/json; charset=utf-8"));
    }

    // ==================== 知识库相关 ====================
    /**
     * 获取当前用户的所有知识库
     */
    public YuQueResponse<List<YuQueRepo>> listAllRepos() {
        String url = YuQueConstant.YUQUE_BASE_URL + "/repos";
        Request request = new Request.Builder()
                .url(url)
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, (Class<List<YuQueRepo>>) new TypeToken<List<YuQueRepo>>(){}.getType());
    }

    /**
     * 获取知识库详情（通过用户名+知识库名称）
     */
    public YuQueResponse<YuQueRepo> getRepoDetail(String username, String repoName) {
        String url = String.format("%s/repos/%s/%s", YuQueConstant.YUQUE_BASE_URL, username, repoName);
        Request request = new Request.Builder()
                .url(url)
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, YuQueRepo.class);
    }

    // ==================== 文档查询相关（原有） ====================
    /**
     * 获取知识库ID（通过用户名+知识库名称）
     */
    public YuQueResponse<Long> getRepoId(String username, String repoName) {
        YuQueResponse<YuQueRepo> repoResponse = getRepoDetail(username, repoName);
        YuQueResponse<Long> response = new YuQueResponse<>();
        if (repoResponse.isSuccess()) {
            response.setSuccess(true);
            response.setData(repoResponse.getData().getId());
        } else {
            response.setSuccess(false);
            response.setErrorMsg(repoResponse.getErrorMsg());
        }
        return response;
    }

    /**
     * 搜索知识库中的文档
     */
    public YuQueResponse<List<YuQueDoc>> searchDocs(Long repoId, String keyword) {
        String url = String.format("%s/repos/%d/docs?keyword=%s", YuQueConstant.YUQUE_BASE_URL, repoId, keyword);
        Request request = new Request.Builder()
                .url(url)
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, (Class<List<YuQueDoc>>) new TypeToken<List<YuQueDoc>>(){}.getType());
    }

    /**
     * 获取单个文档详情
     */
    public YuQueResponse<YuQueDoc> getDocDetail(Long repoId, Long docId) {
        String url = String.format("%s/repos/%d/docs/%d", YuQueConstant.YUQUE_BASE_URL, repoId, docId);
        Request request = new Request.Builder()
                .url(url)
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, YuQueDoc.class);
    }

    // ==================== 文档写操作（新增核心） ====================
    /**
     * 创建新文档
     * @param repoId 知识库ID
     * @param param 文档参数（标题/内容必填）
     */
    public YuQueResponse<YuQueDoc> createDoc(Long repoId, YuQueDocParam param) {
        // 参数校验
        if (param.getTitle() == null || param.getTitle().trim().isEmpty()) {
            YuQueResponse<YuQueDoc> response = new YuQueResponse<>();
            response.setSuccess(false);
            response.setErrorMsg("文档标题不能为空");
            return response;
        }
        if (param.getBody() == null || param.getBody().trim().isEmpty()) {
            YuQueResponse<YuQueDoc> response = new YuQueResponse<>();
            response.setSuccess(false);
            response.setErrorMsg("文档内容不能为空");
            return response;
        }

        String url = String.format("%s/repos/%d/docs", YuQueConstant.YUQUE_BASE_URL, repoId);
        Request request = new Request.Builder()
                .url(url)
                .post(buildJsonRequestBody(param))
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, YuQueDoc.class);
    }

    /**
     * 更新文档（增量修改，保留原有内容）
     * @param repoId 知识库ID
     * @param docId 文档ID
     * @param param 要修改的参数（标题/内容/描述等）
     */
    public YuQueResponse<YuQueDoc> updateDoc(Long repoId, Long docId, YuQueDocParam param) {
        String url = String.format("%s/repos/%d/docs/%d", YuQueConstant.YUQUE_BASE_URL, repoId, docId);
        Request request = new Request.Builder()
                .url(url)
                .put(buildJsonRequestBody(param))
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, YuQueDoc.class);
    }

    /**
     * 覆盖文档（完全替换内容，等同于强制更新）
     * @param repoId 知识库ID
     * @param docId 文档ID
     * @param param 新的文档参数（标题/内容必填）
     */
    public YuQueResponse<YuQueDoc> overwriteDoc(Long repoId, Long docId, YuQueDocParam param) {
        // 覆盖操作强制校验标题和内容
        if (param.getTitle() == null || param.getTitle().trim().isEmpty()) {
            YuQueResponse<YuQueDoc> response = new YuQueResponse<>();
            response.setSuccess(false);
            response.setErrorMsg("覆盖文档时标题不能为空");
            return response;
        }
        if (param.getBody() == null || param.getBody().trim().isEmpty()) {
            YuQueResponse<YuQueDoc> response = new YuQueResponse<>();
            response.setSuccess(false);
            response.setErrorMsg("覆盖文档时内容不能为空");
            return response;
        }
        return updateDoc(repoId, docId, param);
    }

    /**
     * 删除文档
     * @param repoId 知识库ID
     * @param docId 文档ID
     */
    public YuQueResponse<Boolean> deleteDoc(Long repoId, Long docId) {
        YuQueResponse<Boolean> response = new YuQueResponse<>();
        String url = String.format("%s/repos/%d/docs/%d", YuQueConstant.YUQUE_BASE_URL, repoId, docId);
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();

        try {
            Response httpResponse = client.newCall(request).execute();
            if (httpResponse.isSuccessful()) {
                response.setSuccess(true);
                response.setData(true);
            } else {
                String errorMsg = String.format("删除文档失败：%d %s", httpResponse.code(), httpResponse.message());
                response.setSuccess(false);
                response.setErrorMsg(errorMsg);
                log.error(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "删除文档异常：" + e.getMessage();
            response.setSuccess(false);
            response.setErrorMsg(errorMsg);
            log.error(errorMsg, e);
        }
        return response;
    }

    /**
     * 移动文档（修改父文档/归类）
     * @param repoId 知识库ID
     * @param docId 文档ID
     * @param parentId 新的父文档ID（0表示根目录）
     */
    public YuQueResponse<YuQueDoc> moveDoc(Long repoId, Long docId, Long parentId) {
        YuQueDocParam param = new YuQueDocParam();
        param.setParent_id(parentId);

        String url = String.format("%s/repos/%d/docs/%d", YuQueConstant.YUQUE_BASE_URL, repoId, docId);
        Request request = new Request.Builder()
                .url(url)
                .patch(buildJsonRequestBody(param))
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();
        return executeRequest(request, YuQueDoc.class);
    }

    /**
     * 批量操作文档（批量删除/移动，需语雀企业版权限）
     * @param repoId 知识库ID
     * @param docIds 文档ID列表
     * @param operation 操作类型：delete/move
     * @param parentId 移动时的父文档ID（仅move时有效）
     */
    public YuQueResponse<Boolean> batchOperateDocs(Long repoId, List<Long> docIds, String operation, Long parentId) {
        YuQueResponse<Boolean> response = new YuQueResponse<>();
        if (docIds == null || docIds.isEmpty()) {
            response.setSuccess(false);
            response.setErrorMsg("批量操作的文档ID列表不能为空");
            return response;
        }
        if (!"delete".equals(operation) && !"move".equals(operation)) {
            response.setSuccess(false);
            response.setErrorMsg("仅支持 delete/move 两种批量操作");
            return response;
        }

        String url = String.format("%s/repos/%d/docs/batch", YuQueConstant.YUQUE_BASE_URL, repoId);
        YuQueBatchParam batchParam = new YuQueBatchParam();
        batchParam.setIds(docIds);
        batchParam.setOp(operation);
        if ("move".equals(operation)) {
            batchParam.setParent_id(parentId);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(buildJsonRequestBody(batchParam))
                .header(YuQueConstant.AUTH_HEADER, authHeaderValue)
                .build();

        try {
            Response httpResponse = client.newCall(request).execute();
            if (httpResponse.isSuccessful()) {
                response.setSuccess(true);
                response.setData(true);
            } else {
                String errorMsg = String.format("批量操作失败：%d %s", httpResponse.code(), httpResponse.message());
                response.setSuccess(false);
                response.setErrorMsg(errorMsg);
                log.error(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "批量操作异常：" + e.getMessage();
            response.setSuccess(false);
            response.setErrorMsg(errorMsg);
            log.error(errorMsg, e);
        }
        return response;
    }

    // 内部辅助类 - 批量操作参数
    private static class YuQueBatchParam {
        private List<Long> ids;
        private String op;
        private Long parent_id;

        // getter/setter
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public String getOp() { return op; }
        public void setOp(String op) { this.op = op; }
        public Long getParent_id() { return parent_id; }
        public void setParent_id(Long parent_id) { this.parent_id = parent_id; }
    }

    /**
     * 关闭客户端（释放资源）
     */
    public void close() {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }
    }
}