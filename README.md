# 🌆 ReferCity: 基于 AI 与深度数据洞察的校友职场内推平台

> **ReferCity** - 链接 CityU 校友力量，用数据与 AI 铺就求职之路。
> 
> *A Data-Driven Career Intelligence Platform for CityU Community.*

---

## 🎯 1. 产品核心定位 (Product Positioning)
**ReferCity** 不仅仅是一个内推码展示板，它是校友求职的“智能导航仪”。
在当前的求职环境下，海量面经往往是碎片化、低质量的。我们通过 **Java 微服务 + DeepSeek RAG (检索增强生成)** 技术，将“内推资源”与“个性化诊断”结合，实现：
- **精准 Referral**: 港校身份核验，构建高价值校友圈。
- **AI 简历对齐**: 并非通用的修改建议，而是基于**目标岗位真实面经库**的深度比对。
- **数据洞察**: 自动提取面试高频考点，量化求职成功率。

---

## 🏗️ 2. 技术架构图 (System Architecture)
ReferCity 采用典型的**工业级 AI 应用架构**，充分体现 BDA 对数据流转的严谨要求：



- **前端 (Frontend)**: 微信小程序 (提供极致的轻量化体验)。
- **后端 (Backend)**: Java Spring Boot 3.x + MyBatis Plus (核心业务逻辑)。
- **AI 引擎 (AI Engine)**: DeepSeek-V3 API (逻辑推理) + LangChain4j (数据编排)。
- **数据层 (Data Layer)**: 
  - **MySQL**: 存储用户信息、职位数据及内推状态。
  - **Vector Storage**: 存储结构化后的面经嵌入向量，支持语义搜索。
  - **Redis**: 缓存 AI 报告与热点职位，提升响应速度。

---

## 📈 3. BDA 特色功能：AI 一对一面试引导 (Core Feature)
这是 ReferCity 的核心壁垒。不同于普通产品，我们实现了 **“简历-岗位-面经”** 的三向对齐：

1. **结构化提取**: 利用 LLM 将用户上传的 PDF 简历转化为结构化特征向量。
2. **多维评估模型**: 
   $$Match\_Score = \alpha \cdot Skills + \beta \cdot Experience + \gamma \cdot Business\_Sense$$
   其中 $\alpha, \beta, \gamma$ 权重根据不同职能岗位（PM/DA/SDE）动态调整。
3. **针对性预演**: 从面经库中检索出该岗位最近 3 个月的 Top 5 必考点，并让 AI 生成模拟提问。

---

## 🗺️ 4. 实施路线图 (Roadmap)

### 📍 第一阶段：产品定义与数据建模 (Ongoing)
- [x] **品牌升级**: 确立 **ReferCity** 品牌标识与视觉规范。
- [ ] **需求工程**: 编写 `docs/PRD.md`，定义 MVP 版本功能边界。
- [ ] **提示词工程**: 设计并测试针对 BDA 场景的 **"AI Interviewer" System Prompt**。

### 📍 第二阶段：微服务骨架与 AI 链路接入
- [ ] 搭建 Java 核心服务，集成 DeepSeek API。
- [ ] 实现基于 PDF 文本提取的简历解析器。
- [ ] 建立初步的面经知识库 (RAG 基础版)。

### 📍 第三阶段：数据分析与增长实验
- [ ] **面试雷达**: 开发基于 Python 的面试趋势可视化组件。
- [ ] **信用分体系**: 设计校友内推激励模型。

---

## 📂 5. 目录导航
- `/docs`: 产品文档 (PRD, API Spec, Database Schema)
- `/backend-service`: Java Spring Boot 源码
- `/miniprogram`: 小程序前端代码
- `/bda-scripts`: Python 爬虫与面经数据分析脚本

---
**Maintainer**: Zhou Yuqian | **Affiliation**: CityU Business Data Analysis (BDA)
