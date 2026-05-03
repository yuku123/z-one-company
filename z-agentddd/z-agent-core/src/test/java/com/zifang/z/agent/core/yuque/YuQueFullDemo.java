package com.zifang.z.agent.core.yuque;

/**
 * 语雀全功能操作示例
 * 包含：创建/查询/更新/覆盖/移动/删除文档
 */
public class YuQueFullDemo {
    public static void main(String[] args) {
        // 1. 基础配置
        String yuqueToken = "nmOPFzVWwLUpDDCvLT7uhrLg9weG4QEVMNrTQmc7"; // 你的Token（需有读写权限）
        String username = "yuku123"; // 如：zhangsan
        String repoName = "书籍分类"; // 知识库名称

        // 2. 创建客户端
        YuQueClient client = new YuQueClient(yuqueToken);
        Long repoId = null;

        try {
            // ==================== 第一步：获取知识库ID ====================
            YuQueResponse<Long> repoIdResponse = client.getRepoId(username, repoName);
            if (!repoIdResponse.isSuccess()) {
                System.out.println("获取知识库ID失败：" + repoIdResponse.getErrorMsg());
                return;
            }
            repoId = repoIdResponse.getData();
            System.out.println("知识库ID：" + repoId);

            // ==================== 第二步：创建新文档 ====================
            YuQueDocParam createParam = new YuQueDocParam();
            createParam.setTitle("新增心理学著作 - 思考，快与慢");
            createParam.setBody("# 思考，快与慢\n\n作者：丹尼尔·卡尼曼\n\n## 核心内容\n人类的思考分为快思考和慢思考两种模式...");
            createParam.setDescription("诺贝尔经济学奖得主的心理学经典著作");
            createParam.setPublic_(0); // 私有文档

            YuQueResponse<YuQueDoc> createResponse = client.createDoc(repoId, createParam);
            if (createResponse.isSuccess()) {
                YuQueDoc newDoc = createResponse.getData();
                System.out.println("\n===== 创建文档成功 =====");
                System.out.println("文档ID：" + newDoc.getId());
                System.out.println("文档标题：" + newDoc.getTitle());
                System.out.println("文档地址：" + newDoc.getFullUrl(username));
            } else {
                System.out.println("创建文档失败：" + createResponse.getErrorMsg());
                return;
            }
            Long docId = createResponse.getData().getId();

            // ==================== 第三步：更新文档（增量修改） ====================
            YuQueDocParam updateParam = new YuQueDocParam();
            updateParam.setDescription("诺贝尔经济学奖得主丹尼尔·卡尼曼的心理学经典著作，解析人类决策思维"); // 仅修改描述
            YuQueResponse<YuQueDoc> updateResponse = client.updateDoc(repoId, docId, updateParam);
            if (updateResponse.isSuccess()) {
                System.out.println("\n===== 更新文档成功 =====");
                System.out.println("修改后描述：" + updateResponse.getData().getDescription());
            } else {
                System.out.println("更新文档失败：" + updateResponse.getErrorMsg());
            }

            // ==================== 第四步：覆盖文档（完全替换内容） ====================
            YuQueDocParam overwriteParam = new YuQueDocParam();
            overwriteParam.setTitle("思考，快与慢（完整版）"); // 覆盖标题
            overwriteParam.setBody("# 思考，快与慢\n\n## 作者简介\n丹尼尔·卡尼曼（Daniel Kahneman）...\n\n## 核心章节\n1. 系统1与系统2\n2. 直觉与偏见\n3. 决策与风险"); // 覆盖全部内容
            YuQueResponse<YuQueDoc> overwriteResponse = client.overwriteDoc(repoId, docId, overwriteParam);
            if (overwriteResponse.isSuccess()) {
                System.out.println("\n===== 覆盖文档成功 =====");
                System.out.println("新标题：" + overwriteResponse.getData().getTitle());
            } else {
                System.out.println("覆盖文档失败：" + overwriteResponse.getErrorMsg());
            }

            // ==================== 第五步：移动文档（归类到根目录） ====================
//            YuQueResponse<YuQueDoc> moveResponse = client.moveDoc(repoId, docId, 0L);
//            if (moveResponse.isSuccess()) {
//                System.out.println("\n===== 移动文档成功 =====");
//                System.out.println("文档新父ID：" + moveResponse.getData().getParent_id());
//            } else {
//                System.out.println("移动文档失败：" + moveResponse.getErrorMsg());
//            }

            // ==================== 第六步：批量操作（示例：批量删除，注释掉避免误删） ====================
            // List<Long> batchDocIds = Arrays.asList(docId);
            // YuQueResponse<Boolean> batchResponse = client.batchOperateDocs(repoId, batchDocIds, "delete", null);
            // if (batchResponse.isSuccess()) {
            //     System.out.println("\n===== 批量删除成功 =====");
            // } else {
            //     System.out.println("批量删除失败：" + batchResponse.getErrorMsg());
            // }

            // ==================== 第七步：删除文档（按需执行） ====================
            // YuQueResponse<Boolean> deleteResponse = client.deleteDoc(repoId, docId);
            // if (deleteResponse.isSuccess()) {
            //     System.out.println("\n===== 删除文档成功 =====");
            // } else {
            //     System.out.println("删除文档失败：" + deleteResponse.getErrorMsg());
            // }

        } finally {
            // 关闭客户端
            client.close();
        }
    }
}