# 在线考试系统交付计划（从0到上线）

本方案基于需求清单，整理为可直接落地的架构与迭代计划，便于团队并行开发与排期。

## 1. 目标与范围
- 角色：管理员、教师/阅卷员、考生。
- 端：PC 管理端（Vue3）、考生端（uni-app H5/小程序/APP）。
- 核心链路：题库 → 试卷（人工/随机）→ 发布考试 → 开考生成 attempt + snapshot → 作答保存 → 交卷 → 客观判分 → 主观阅卷 → 成绩发布 → 报表。

## 2. 架构与分层
- **技术栈**：Java + Spring Boot，MySQL，Redis，前端 Vue3 + uni-app。
- **推荐 Maven 多模块**：
  - `common`: core/web/security/redis/file/mybatis 通用能力。
  - `sys`: auth/base/notify/quartz/web。
  - `exam`: api/core/report。
- **分层规范**：controller（校验 + 鉴权）→ service（事务、编排）→ domain（规则）→ repository/mapper（数据访问）→ dto/vo → infra（缓存、消息、第三方）。

## 3. 数据库核心表
- **RBAC**：sys_user、sys_role、sys_menu、sys_user_role、sys_role_menu、sys_dept、sys_dict、sys_param。
- **题库**：ex_question、ex_question_option、ex_tag、ex_question_tag、ex_asset。
- **试卷**：ex_paper、ex_paper_question、ex_paper_rule（随机规则）。
- **考试与准入**：ex_exam、ex_exam_whitelist。
- **作答与阅卷**：ex_attempt、ex_answer、ex_mark_task、ex_score（可选）。
- **审计与风控**：ex_exam_event_log、ex_risk_flag。
- **关键约束**：ex_answer 唯一索引 (attempt_id, question_id)；attempt 条件更新限制 status=IN_PROGRESS。

## 4. 核心能力设计
### 4.1 试卷快照
- 内容：卷面结构、抽题结果、题目与选项文本快照、乱序信息、正确答案、判分规则 `gradingPolicy`、渲染策略 `renderPolicy`。
- 存储：attempt 记录 seed；snapshot JSON 写入 attempt，防止改题争议。

### 4.2 状态机与并发
- attempt 状态：IN_PROGRESS → SUBMITTED → MARKING → SCORED → EXPIRED。
- 幂等：
  - start：exam+user 只允许一个进行中 attempt。
  - save：同题覆盖，Redis 1s 节流。
  - submit：条件更新 where status=IN_PROGRESS，成功后判客观。
- 锁与事务：submit/mark-submit 事务内落库与汇总。

### 4.3 随机组卷
- 避免 `ORDER BY RAND()`；优先使用 ID 池洗牌或预生成 rand_key。
- 降级：严格匹配题型+标签+难度 → 去难度/标签 → 不足则提示题库不足。
- 可复现：attempt.seed 驱动抽题与乱序。

### 4.4 判分与阅卷
- 答案统一 JSON：choice/blanks/text。
- 客观题：单选/判断相等满分；多选集合相等；填空支持大小写/空格忽略与部分分。
- 主观题：按 attempt 建阅卷任务，支持复核覆盖分数并记录日志。

### 4.5 反作弊与风控
- 低成本：切屏/失焦计数、IP/设备记录、作答事件日志、极短时高分风险标记。
- 增强：题目/选项乱序、限制同账号在线、设备绑定。

### 4.6 缓存与性能
- 缓存：exam meta、paper 结构、attempt snapshot（注意一致性）、字典/参数。
- Redis key 规范示例：`exam:{examId}:meta`、`attempt:{attemptId}:lock`、`attempt:{attemptId}:saveThrottle`、`user:{userId}:session`。
- 高并发保存：增量提交，后端 upsert ex_answer，必要时文本压缩。

### 4.7 报表与分析
- 报表：成绩列表、分数段分布、及格率、题目正确率/区分度、标签掌握度、组织维度对比。
- 实现：小数据实时 SQL；中数据异步聚合写 ex_report_*；大数据离线任务。

### 4.8 导入导出与文件
- 题库 Excel 导入校验题型/答案；失败行输出错误明细。
- 成绩导出包含姓名/账号/分数/客观主观/交卷时间/风险标记。
- 题干富文本存 HTML；文件上传通过 sys-file 抽象。

### 4.9 消息通知
- 场景：发布考试、阅卷任务、出分通知。
- 渠道：站内信为主，可扩展企业微信/邮件/短信。

### 4.10 权限与多租户
- 菜单、按钮、数据权限（按 dept/class）。
- 多租户可选 tenant_id 字段隔离，视部署模式启用。

## 5. 接口概览
- Auth：登录、注销、当前用户、菜单。
- 管理端题库：分页、CRUD、导入导出、标签管理。
- 管理端试卷：分页、创建/更新、加题、设置随机规则、预览。
- 考试发布：创建/更新、发布/关闭、白名单导入、进度查询。
- 考生端：考试列表/详情、start→attempt+snapshot、save、submit、查看 snapshot 与成绩。
- 阅卷：任务分页、领取、提交评分与评语、退回、复核。
- 报表：成绩列表、分布、题目统计、导出。

## 6. 迭代排期
### P0（能用，2~3 周）
- RBAC + 登录；题库 CRUD；人工组卷；发布考试；考生 start/save/submit；客观题判分；成绩查询。

### P1（产品化，2~3 周）
- 随机组卷及降级提示；主观题阅卷与复核；基础报表（正确率/分布）；题库与成绩导入导出。

### P2（成熟度提升，3~4 周）
- 练习/错题本/收藏；风控（切屏、风险标记）；消息通知与定时任务；数据权限；可选多租户。

## 7. 测试与部署
- 单元：判分规则、随机组卷、状态机。
- 集成：start/save/submit 幂等；阅卷提交事务。
- 压测：保存、交卷接口 QPS；Redis 命中率监控。
- 部署：Nginx + Docker 化服务，MySQL 主从可选，Redis 单机/哨兵，对象存储 MinIO/OSS/COS；监控接口耗时、错误码、考试峰值、慢 SQL、关键事件。

## 8. 下一步
- 若需直接编码，可生成完整 OpenAPI 3.0 定义与 Java DTO/Service 模板作为起步骨架。
