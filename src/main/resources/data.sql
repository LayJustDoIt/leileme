INSERT IGNORE INTO content_category
(id, parent_id, category_code, category_name, icon, description, sort_no, status)
VALUES
(1,0,'HOT','热搜资讯','fire','全网热点与每日资讯',100,1),
(2,0,'CREATOR','自媒体创作','video','选题、标题、文案与脚本',90,1),
(3,0,'AI_TOOL','AI 工具','robot','AI 与效率工具',80,1),
(4,0,'LEARNING','学习提升','book','学习、职场与副业成长',70,1),
(5,0,'LIFE','娱乐生活','sofa','摸鱼、亲子与轻松内容',60,1),
(6,0,'RANDOM','随机建议','dice','不知道干嘛时的小建议',10,1);

INSERT IGNORE INTO hot_keyword
(id, keyword, display_name, icon, sort_no, status)
VALUES
(1,'今日热搜','今日热搜','🔥',100,1),
(2,'爆款脚本','爆款脚本',NULL,90,1),
(3,'AI工具','AI工具',NULL,80,1),
(4,'副业灵感','副业灵感',NULL,70,1),
(5,'摸鱼指南','摸鱼指南',NULL,60,1),
(6,'Java面试','Java面试',NULL,50,1);

INSERT IGNORE INTO audience
(id, audience_code, audience_name, description, sort_no, status)
VALUES
(1,'OFFICE_WORKER','上班族','摸鱼、职场、效率与下班生活',100,1),
(2,'CREATOR','自媒体','选题、脚本、标题与爆款案例',90,1),
(3,'STUDENT','学生党','学习资源、校园生活与成长灵感',80,1),
(4,'PARENT','宝妈专区','亲子学习、育儿工具与家庭安排',70,1),
(5,'GENERAL','泛娱乐用户','热点、娱乐与随机发现',60,1);

INSERT IGNORE INTO content_tag
(id, tag_name, tag_code, use_count, status)
VALUES
(1,'今日热搜','hot-search',10,1),(2,'爆款文案','viral-copy',20,1),
(3,'短视频','short-video',18,1),(4,'AI工具','ai-tool',16,1),
(5,'副业','side-job',15,1),(6,'上班族','office-worker',14,1),
(7,'学习','learning',12,1),(8,'摸鱼','slacking',11,1),
(9,'小红书','xiaohongshu',10,1),(10,'Java面试','java-interview',8,1),
(11,'宝妈','parenting',7,1),(12,'效率','productivity',13,1);

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1001,'HOT_NEWS',1,'今日全网热搜速览','微博、知乎、抖音今日热点聚合，三分钟了解全网最关注的话题。','<h2>今日全网热搜速览</h2><p>今天全网讨论度最高的话题集中在科技发布会、社会民生与娱乐综艺三个方向，热搜榜单变化较快，下面挑出几条最具代表性的内容速览。</p><h2>科技与互联网</h2><p>多家手机厂商同步官宣下个月的新品发布会，AI 影像与续航成为关键词；某互联网大厂宣布新一轮内部架构调整，重点关注 AI 业务整合。</p><h2>社会与民生</h2><p>多地公布高温预警，建议市民减少午后户外活动；某城市地铁新线开通试运营，首日客流超出预期。</p><h2>娱乐与综艺</h2><p>一档综艺节目的录制花絮登上热搜，相关话题阅读量破亿；某部新剧口碑持续发酵，剧粉与路人讨论并存。</p><h2>一句话点评</h2><p>今天的热点偏轻松，适合通勤路上快速浏览，不必深究细节。</p>','热点 热搜 今日新闻 微博 知乎','https://placehold.co/600x360?text=1001','微博热搜','https://example.com/content/1001','热点编辑',1,1,1,98,38620,386,38,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1002,'HOT_NEWS',1,'今天值得关注的 8 条科技新闻','从 AI 模型更新到大厂组织调整，今天科技圈这 8 条动态值得一看。','<h2>今天值得关注的 8 条科技新闻</h2><p>今天的科技新闻主要围绕 AI 模型迭代、硬件新品与互联网公司战略调整展开，整理 8 条最值得关注的动态。</p><h2>AI 与模型</h2><p>某头部团队开源了新的多模态模型，强调中文场景理解能力；另有厂商发布面向开发者的轻量推理 API，免费额度较高。</p><h2>硬件与数码</h2><p>新款笔记本普遍搭载 NPU，主打本地 AI 推理；折叠屏手机价格继续下探，万元以内选择增多。</p><h2>互联网公司动态</h2><p>多家公司公布季度财报，AI 相关收入占比提升；部分公司调整组织架构，整合产品线以应对竞争。</p><h2>开发者与开源</h2><p>几个热门开源项目发布重大版本更新，社区贡献者数量稳步增长，文档与生态进一步完善。</p><h2>小结</h2><p>整体趋势上，AI 仍是科技新闻的主线，硬件则进入算力落地的阶段。</p>','科技新闻 AI 互联网 数码','https://placehold.co/600x360?text=1002','36氪','https://example.com/content/1002','科技观察员',1,1,1,91,22810,228,22,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1003,'HOT_NEWS',1,'上班摸鱼也能看懂的财经热点','用大白话讲清楚今天的财经关键词，不用懂专业术语也能看明白。','<h2>上班摸鱼也能看懂的财经热点</h2><p>今天财经圈在讨论的几件事，翻译成大白话就是下面这些。</p><h2>降息与利率</h2><p>简单说，利率变动直接影响房贷和企业借钱成本。如果预期降息，股市通常会有反应，债市也会跟着波动。</p><h2>汇率变化</h2><p>汇率涨跌影响进口商品价格和出国旅游成本，对做外贸和海淘的人来说感受更明显。</p><h2>股市与板块</h2><p>今天涨幅靠前的板块集中在科技与消费，资金在题材股之间轮动较快，短期博弈气氛浓。</p><h2>一句建议</h2><p>看不懂的别追，看得懂的也别全押，分散配置永远比孤注一掷更稳。</p>','财经 热点 上班族 摸鱼','https://placehold.co/600x360?text=1003','虎嗅','https://example.com/content/1003','财经搬运工',1,1,0,82,15430,154,15,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1004,'COPYWRITING',2,'短视频爆款开头的 10 种写法','总结 10 种经过验证的短视频开头结构，套上自己的内容就能用。','<h2>短视频爆款开头的 10 种写法</h2><p>短视频的前 3 秒决定生死，下面这 10 种开头结构都来自实际爆款案例，可以按内容类型套用。</p><h2>提问式开头</h2><p>用一个具体的问题切入，比如「为什么你的简历总是石沉大海」，直接勾起目标人群的好奇心。</p><h2>反常识式开头</h2><p>先抛出一个看似错误的观点，再给出解释，比如「早起并不会让你更高效」。</p><h2>数字承诺式开头</h2><p>明确告诉观众能拿到什么，比如「3 个动作，缓解久坐腰酸」。</p><h2>场景代入式开头</h2><p>描述一个观众熟悉的场景，比如「周五下班，你打开外卖 App 不知道吃什么」。</p><h2>对比冲突式开头</h2><p>用两个反差极大的对象制造冲突，比如「月薪 3 千和月薪 3 万的人差在哪」。</p><h2>故事悬念式开头</h2><p>从一个具体的人或事件讲起，留下悬念让观众想看到结局。</p><h2>其他几种开头</h2><ul><li>金句式：一句有力的话直接开场</li><li>问题诊断式：列出症状再给方案</li><li>权威背书式：引用数据或专家观点</li><li>情绪共鸣式：先共情再展开</li></ul><h2>使用建议</h2><p>开头写完后自己念一遍，如果 3 秒内没吸引到自己，就再改一版。</p>','爆款文案 短视频脚本 开头 抖音 小红书 自媒体','https://placehold.co/600x360?text=1004','知乎热门','https://example.com/content/1004','内容研究员',1,1,1,99,52800,528,52,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1005,'COPYWRITING',2,'30 个让人忍不住点开的标题模板','整理 30 个高点击率标题公式，覆盖干货、情绪、悬念等不同方向。','<h2>30 个让人忍不住点开的标题模板</h2><p>好标题不是炫技，而是把内容价值精准传递给对的人。下面按方向分类整理 30 个可直接套用的模板。</p><h2>干货类</h2><ul><li>X 个方法，帮你解决 XX 问题</li><li>从 0 到 1 搞定 XX，新手也能学会</li><li>XX 高频问题合集，一次性讲清楚</li></ul><h2>情绪类</h2><ul><li>那些 XX 的人，后来都怎么样了</li><li>别再 XX 了，真的会后悔</li><li>成年人的 XX，都是悄无声息的</li></ul><h2>悬念类</h2><ul><li>我试了 XX，结果出人意料</li><li>原来 XX 背后还有这种事</li><li>这件事，90% 的人都搞错了</li></ul><h2>对比类</h2><ul><li>月薪 X 千和 X 万的人差在哪</li><li>同样是 XX，差距怎么这么大</li><li>3 年前 vs 3 年后，我变了这些</li></ul><h2>使用提示</h2><p>把模板里的 XX 替换成自己的关键词，先保证不夸大、不违规，再追求吸引力。</p>','标题 爆款 标题模板 文案 自媒体','https://placehold.co/600x360?text=1005','小红书热门','https://example.com/content/1005','文案研究社',1,1,1,96,47920,479,47,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1006,'COPYWRITING',2,'口播视频万能脚本结构','一套适合口播类视频的通用脚本结构，从开场到引导互动都帮你拆好了。','<h2>口播视频万能脚本结构</h2><p>口播视频看似简单，但结构不清晰会让观众中途流失。下面这套结构适合大多数知识分享与观点类内容。</p><h2>第一段：钩子（前 3 秒）</h2><p>用一个具体问题或反常识结论抓住注意力，比如「别再用这种方式存钱了」。</p><h2>第二段：共情（4 到 10 秒）</h2><p>说出观众的痛点或场景，让他觉得「这说的就是我」。</p><h2>第三段：核心观点（10 到 30 秒）</h2><p>给出你的主结论，最好一句话能讲清楚，便于记忆和传播。</p><h2>第四段：论据与案例（30 到 60 秒）</h2><p>用 1 到 2 个具体案例支撑观点，避免空喊口号。</p><h2>第五段：行动引导（最后 5 秒）</h2><p>告诉观众下一步做什么，比如关注、收藏或评论某个关键词。</p><h2>落地建议</h2><p>写完脚本后通读一遍，删掉所有不影响理解的句子，节奏会紧很多。</p>','口播 脚本 短视频 文案 视频号','https://placehold.co/600x360?text=1006','B站精选','https://example.com/content/1006','创作教练',1,1,0,92,33750,337,33,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1007,'COPYWRITING',2,'小红书种草文案的 6 个结构','拆解 6 种小红书种草文案结构，覆盖好物分享、生活方式与对比测评。','<h2>小红书种草文案的 6 个结构</h2><p>小红书的种草文案有比较明显的套路，掌握下面 6 种结构，新手也能写出有质感的笔记。</p><h2>结构一：痛点 + 方案 + 效果</h2><p>先讲自己遇到的问题，再介绍用的产品，最后描述使用后的变化，逻辑清晰。</p><h2>结构二：场景代入 + 推荐 + 细节</h2><p>从一个生活场景切入，自然带出推荐物，再补充使用细节增加可信度。</p><h2>结构三：对比测评 + 结论</h2><p>把同类产品放在一起比较，给出明确推荐，适合决策类内容。</p><h2>结构四：清单合集 + 简评</h2><p>列出多个推荐项，每项用一两句话点评，方便收藏。</p><h2>结构五：故事线 + 种草</h2><p>用一段经历串起推荐，情感色彩更浓，适合生活方式类内容。</p><h2>结构六：问答式 + 重点加粗</h2><p>以常见问题为线索展开，重点信息加粗或加 emoji，方便快速浏览。</p><h2>通用技巧</h2><p>标题要具体，首图要真实，文末记得引导互动，比如「你用过哪个，评论区聊聊」。</p>','小红书 种草 文案 爆款脚本','https://placehold.co/600x360?text=1007','小红书热门','https://example.com/content/1007','种草研究员',1,1,0,88,29110,291,29,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1008,'COPYWRITING',2,'本周短视频热门选题清单','整理本周表现最好的短视频选题方向，附上选题切入角度。','<h2>本周短视频热门选题清单</h2><p>本周短视频平台热度较高的选题集中在下面几个方向，按方向整理并附上切入角度。</p><h2>职场方向</h2><ul><li>打工人的下班日常：突出真实感</li><li>薪资话题：用数据对比制造讨论</li><li>职场沟通：以具体场景为例</li></ul><h2>情绪方向</h2><ul><li>成年人的崩溃瞬间：引发共鸣</li><li>自我和解类内容：治愈向</li><li>关于朋友与孤独：有金句易传播</li></ul><h2>AI 与效率方向</h2><ul><li>AI 工具实测：突出可复制性</li><li>办公技巧合集：实用导向</li><li>AI 替代焦虑讨论：制造话题</li></ul><h2>副业方向</h2><ul><li>低门槛副业盘点：吸引新手</li><li>副业踩坑实录：真实案例</li><li>时间管理：与副业结合</li></ul><h2>选题建议</h2><p>选题方向只是参考，关键还是切入角度要独特，避免和已有爆款完全撞车。</p>','热门选题 自媒体 短视频 灵感','https://placehold.co/600x360?text=1008','抖音精选','https://example.com/content/1008','选题编辑',1,1,1,94,41200,412,41,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1009,'TOOL',3,'本周值得收藏的 AI 工具','精选本周更新的几款 AI 工具，覆盖写作、绘图与办公场景。','<h2>本周值得收藏的 AI 工具</h2><p>本周有几款 AI 工具更新比较值得关注，按使用场景分类整理，方便按需收藏。</p><h2>写作与文案</h2><p>某款写作助手新增了选题建议功能，输入关键词就能生成多个角度的标题与大纲，适合自媒体选题阶段使用。</p><h2>图像与设计</h2><p>一款 AI 绘图工具上线了中文 prompt 优化，对不擅长写英文描述的用户更友好，出图质量稳定。</p><h2>办公与效率</h2><p>某办公套件集成了 AI 摘要与表格公式生成，处理长文档和复杂表格时能省不少时间。</p><h2>开发者向</h2><p>一款面向开发者的代码助手更新了对新框架的支持，并优化了上下文窗口，长项目下的理解能力提升。</p><h2>使用建议</h2><p>工具是辅助，关键还是工作流。建议先选一款深入用熟，再考虑加新工具。</p>','AI工具 效率 办公 内容创作','https://placehold.co/600x360?text=1009','少数派','https://example.com/content/1009','工具测评员',1,1,1,97,50320,503,50,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1010,'TOOL',3,'5 个免费图片素材网站','整理 5 个高质量免费图片素材网站，做封面、海报和配图都够用。','<h2>5 个免费图片素材网站</h2><p>做自媒体最头疼的就是找配图，下面这 5 个网站图片质量高、授权清晰，日常使用基本够用。</p><h2>Unsplash</h2><p>老牌免费图库，摄影风格为主，适合做封面和氛围图，分类检索比较方便。</p><h2>Pexels</h2><p>图片和视频都有，授权宽松，搜索体验好，适合找生活化场景素材。</p><h2>Pixabay</h2><p>图片、矢量图、插画都能找到，内容偏通用，适合多场景使用。</p><h2>Foodiesfeed</h2><p>专注美食类图片，做餐饮或美食类内容时非常实用。</p><h2>Freerange</h2><p>风格偏自然与生活，更新稳定，适合找一些不那么千篇一律的素材。</p><h2>使用提示</h2><p>免费图库也要注意授权说明，部分图片需要署名，商用前最好再确认一次。</p>','图片素材 免费网站 自媒体 设计','https://placehold.co/600x360?text=1010','少数派','https://example.com/content/1010','设计资源库',1,1,0,84,18330,183,18,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1011,'TOOL',3,'不用安装的在线 PDF 工具','合并、压缩、转换、拆分，常用的 PDF 操作这些在线工具都能搞定。','<h2>不用安装的在线 PDF 工具</h2><p>偶尔处理 PDF 不用专门装软件，下面这几个在线工具覆盖了最常见的操作需求。</p><h2>合并与拆分</h2><p>支持把多个 PDF 合并成一个，或把一个 PDF 拆成多段，操作拖拽即可完成。</p><h2>压缩</h2><p>压缩工具可以按清晰度档位选择，邮件附件和上传资料时很实用。</p><h2>格式转换</h2><p>支持 PDF 与 Word、Excel、图片之间互转，转换后排版保留度较好。</p><h2>编辑与签名</h2><p>部分工具支持直接在 PDF 上加文字、画线和签名，适合处理合同与表单。</p><h2>安全提示</h2><p>涉及隐私的文档建议使用本地工具，或选择明确不上传服务器的在线方案。</p>','PDF工具 办公 效率 在线工具','https://placehold.co/600x360?text=1011','少数派','https://example.com/content/1011','效率工具箱',1,1,0,79,14210,142,14,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1012,'TOOL',3,'适合中文创作的 AI 写作工具','盘点几款对中文支持较好的 AI 写作工具，从选题到改写都能用上。','<h2>适合中文创作的 AI 写作工具</h2><p>AI 写作工具很多，但对中文场景支持差异较大，下面几款在中文创作上表现相对稳定。</p><h2>选题与大纲</h2><p>某款工具支持输入主题后生成多角度选题与大纲，适合做内容规划阶段使用。</p><h2>正文生成</h2><p>一款主打长文生成的工具，对中文行文风格调教较好，生成内容偏自然，不像翻译腔。</p><h2>改写与润色</h2><p>支持把口语化内容改写成书面表达，也能调整语气风格，适合做二次加工。</p><h2>标题与文案</h2><p>有工具专门做标题与短文案生成，输入关键词就能给出多个版本，方便对比挑选。</p><h2>使用建议</h2><p>AI 生成的文字一定要自己再过一遍，避免出现事实错误或套路化表达。</p>','AI写作 文案 工具 自媒体','https://placehold.co/600x360?text=1012','36氪','https://example.com/content/1012','内容工具观察',1,1,0,90,25880,258,25,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1013,'GUIDE',4,'10 个适合普通人的副业方向','从时间投入、启动成本和可复制性三个维度，筛出 10 个对普通人友好的副业方向。','<h2>10 个适合普通人的副业方向</h2><p>副业不是越多越好，而是要匹配自己的时间与技能。下面这 10 个方向按门槛和回报分了类。</p><h2>低门槛方向</h2><ul><li>短视频剪辑：接单需求大，工具上手快</li><li>代写与文案：有文字功底就能起步</li><li>线上陪聊与咨询：擅长倾听即可</li></ul><h2>技能变现方向</h2><ul><li>设计与排版：会基础设计软件即可</li><li>程序外包：有开发经验的人优势明显</li><li>翻译：外语能力是核心门槛</li></ul><h2>内容积累方向</h2><ul><li>自媒体账号：长线收益但需要耐心</li><li>知识付费：把经验打包成课程</li><li>社群运营：适合有组织能力的人</li></ul><h2>资源变现方向</h2><ul><li>二手转卖：低买高卖赚差价</li></ul><h2>选择建议</h2><p>先评估自己每周能投入的时间，再选方向。副业初期别太在意收入，先跑通流程更重要。</p>','副业 赚钱 普通人 上班族','https://placehold.co/600x360?text=1013','知乎热门','https://example.com/content/1013','副业观察员',1,1,1,95,62100,621,62,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1014,'GUIDE',4,'下班后如何保持两小时专注','一套适合上班族的低压力专注方法，不用硬撑也能坚持两小时。','<h2>下班后如何保持两小时专注</h2><p>下班后还想学点东西，但常常坐下来就犯困。下面这套方法主打低压力，容易坚持。</p><h2>先恢复再学习</h2><p>到家别立刻坐下学习，先用 15 分钟洗澡或散步，把工作状态切换掉。</p><h2>分块而不是硬撑</h2><p>两小时拆成 4 个 25 分钟的番茄钟，每段之间休息 5 分钟，比一次性硬坐两小时有效得多。</p><h2>环境要专一</h2><p>固定一个学习位置，桌面只放学习用品，手机放到看不见的地方。</p><h2>任务要具体</h2><p>别写「学习英语」这种模糊任务，改成「背 30 个单词 + 做 1 篇阅读」，执行阻力小很多。</p><h2>允许偶尔失败</h2><p>某天实在学不进去就休息，别因为一次中断就放弃整个计划。</p>','上班族 学习 专注 效率','https://placehold.co/600x360?text=1014','知乎热门','https://example.com/content/1014','学习方法研究',1,1,0,86,22010,220,22,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1015,'GUIDE',4,'零基础做自媒体的第一周计划','一周七天，每天只做一件关键事，帮你把自媒体从想法推进到第一条内容。','<h2>零基础做自媒体的第一周计划</h2><p>很多人想做自媒体但一直没开始，下面这份一周计划每天只做一件关键事，帮你真正迈出第一步。</p><h2>第一天：确定方向</h2><p>选一个自己能持续输出的领域，比如职场、美食或学习，先别纠结平台。</p><h2>第二天：研究对标</h2><p>找 3 到 5 个同方向的账号，看他们最近 10 条内容，记录选题角度和呈现方式。</p><h2>第三天：定平台</h2><p>根据内容形式选平台，图文优先小红书，视频优先抖音或 B 站，别贪多。</p><h2>第四天：准备工具</h2><p>注册账号、完善资料、下载必备工具，把基础环境搭好。</p><h2>第五天：写第一条</h2><p>按对标的风格写一条内容，不用追求完美，先发出去再说。</p><h2>第六七天：复盘</h2><p>看数据反馈，记录哪里可以改进，然后规划第二周的内容。</p>','自媒体 入门 计划 副业','https://placehold.co/600x360?text=1015','小红书热门','https://example.com/content/1015','新人引导员',1,1,0,93,35980,359,35,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1016,'GUIDE',4,'Java 面试高频问题整理','整理 Java 后端面试高频考点，按知识点分类，方便快速复习。','<h2>Java 面试高频问题整理</h2><p>Java 后端面试的考点比较固定，下面按模块整理高频问题，方便临面试前快速过一遍。</p><h2>基础与集合</h2><p>HashMap 的底层结构与扩容机制几乎必问，线程安全的 Map 用 ConcurrentHashMap，要能讲清楚分段锁的演变。</p><h2>并发与多线程</h2><p>线程池的核心参数、拒绝策略、AQS 原理是高频点，synchronized 与 Lock 的区别也常被问。</p><h2>JVM</h2><p>内存区域划分、垃圾回收算法与常用回收器、类加载机制是三大重点，最好能结合实际调优场景讲。</p><h2>框架与中间件</h2><p>Spring 的 IOC 与 AOP 原理、Bean 生命周期，MySQL 索引与事务隔离级别，Redis 常用数据结构与缓存问题。</p><h2>复习建议</h2><p>不要只背答案，结合项目经历讲应用场景，面试官更看重理解深度。</p>','Java 面试 后端 学习 程序员','https://placehold.co/600x360?text=1016','博客园','https://example.com/content/1016','后端老司机',1,1,0,87,24600,246,24,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1017,'GUIDE',4,'学生党高效记笔记的方法','几种适合学生的笔记方法，重点是减少抄写、加强理解。','<h2>学生党高效记笔记的方法</h2><p>记笔记不是抄书，下面几种方法都是围绕「少写多想」设计的，适合学生党日常使用。</p><h2>康奈尔笔记法</h2><p>把页面分成笔记区、线索区和总结区，课上记笔记，课后整理线索，最后写一句话总结。</p><h2>思维导图法</h2><p>适合梳理知识结构，先写核心概念，再向外延展细节，复习时一眼能看全貌。</p><h2>关键词法</h2><p>只记关键词和关键公式，课后用自己的话补全，比逐字记录高效得多。</p><h2>问答式笔记</h2><p>把每个知识点变成一个问题，复习时先尝试回答，再对照答案，主动回忆效果更好。</p><h2>使用建议</h2><p>方法不用贪多，选一种坚持一学期再调整，比频繁换方法有效。</p>','学生 学习 笔记 效率','https://placehold.co/600x360?text=1017','知乎热门','https://example.com/content/1017','学习方法分享',1,1,0,76,12800,128,12,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1018,'GUIDE',5,'亲子阅读的 7 个轻松方法','7 个不用打卡也能坚持的亲子阅读小方法，主打轻松和陪伴。','<h2>亲子阅读的 7 个轻松方法</h2><p>亲子阅读不是任务，而是一段陪伴时光。下面 7 个方法都主打低压力，不用打卡也能坚持。</p><h2>固定一个阅读角</h2><p>在家里找一个舒服的小角落，放上孩子喜欢的书，让阅读变成一种自然选择。</p><h2>每天十分钟就好</h2><p>不用追求读多久，每天十分钟足够，重点是养成习惯而不是完成任务。</p><h2>让孩子选书</h2><p>带孩子去书店或图书馆，让他自己挑感兴趣的书，参与感更强。</p><h2>边读边聊</h2><p>读到有趣的地方停下来聊几句，比单纯念完更有助于理解。</p><h2>角色扮演</h2><p>把书里的故事演出来，孩子会更投入，记忆也更深刻。</p><h2>重复阅读没关系</h2><p>孩子要求重复读同一本是很正常的，重复本身就是一种学习。</p><h2>父母也读</h2><p>孩子在看的时候，大人也拿本书看，比说一百遍「快去读书」有用。</p>','宝妈 亲子 学习 阅读 育儿','https://placehold.co/600x360?text=1018','小红书热门','https://example.com/content/1018','育儿经验分享',1,1,0,80,16720,167,16,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1019,'HOT_NEWS',5,'今天适合摸鱼看的 12 个趣闻','12 条轻松不费脑的趣闻，工作间隙看一眼，刚好够换换脑子。','<h2>今天适合摸鱼看的 12 个趣闻</h2><p>工作间隙想换换脑子，下面这 12 条趣闻轻松不费脑，刚好够摸个鱼。</p><h2>动物冷知识</h2><p>猫咪的鼻纹和人类指纹一样独一无二；章鱼有三颗心脏，其中两颗在游泳时会停止跳动。</p><h2>生活冷知识</h2><p>圆珠笔的笔珠最早用于二战时的坦克瞄准镜；便利贴的胶是失败实验的副产品。</p><h2>历史小故事</h2><p>古希腊的奥运会上运动员是裸体参赛的；最早的闹钟只能在凌晨 4 点响。</p><h2>地理与自然</h2><p>撒哈拉沙漠偶尔也会下雪；世界上跨度最大的拱桥在中国。</p><h2>一句话点评</h2><p>看完这些，你今天的摸鱼 KPI 就算完成了。</p>','摸鱼 娱乐 趣闻 上班族','https://placehold.co/600x360?text=1019','豆瓣','https://example.com/content/1019','摸鱼编辑',1,1,0,89,31500,315,31,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1020,'GUIDE',1,'五分钟恢复注意力的小练习','几个五分钟内能做完的小练习，疲惫时先恢复再工作，效率更高。','<h2>五分钟恢复注意力的小练习</h2><p>疲惫的时候硬撑效率很低，不如花五分钟做下面这些小练习，先把状态找回来。</p><h2>20-20-20 护眼法</h2><p>每 20 分钟看 20 英尺（约 6 米）外的物体 20 秒，缓解眼睛疲劳，注意力也会跟着回血。</p><h2>4-7-8 呼吸法</h2><p>吸气 4 秒、屏息 7 秒、呼气 8 秒，循环 4 次，能快速平静神经。</p><h2>起身倒水</h2><p>离开工位走两步，倒杯水喝一口，身体活动后大脑会清醒不少。</p><h2>迷你冥想</h2><p>闭眼，把注意力放在呼吸上，不走神地保持 1 分钟，等于给大脑重启。</p><h2>望远处发呆</h2><p>找一扇窗户，盯远处某个点发呆 30 秒，不需要思考任何事。</p>','放松 注意力 上班族 摸鱼','https://placehold.co/600x360?text=1020','知乎热门','https://example.com/content/1020','效率研究员',1,1,0,85,19400,194,19,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1021,'RANDOM_TIP',6,'站起来走五分钟','别给自己安排任务，只是单纯地站起来活动一下。','<h2>站起来走五分钟</h2><p>坐久了肩颈会僵，脑子也会变钝。现在放下手里的活，站起来走五分钟。</p><h2>为什么是五分钟</h2><p>不用换衣服也不用下楼，五分钟刚好够让血液循环起来，又不会打断节奏。</p><h2>怎么走</h2><p>不限姿势，去倒杯水、上个厕所、望一眼窗外都算，重点是离开椅子。</p><h2>走完之后</h2><p>回来先做一件小事找状态，比如回一封简单的消息，再进入主线任务。</p>','随机建议 放松','https://placehold.co/600x360?text=1021','豆瓣','https://example.com/content/1021','热心网友',1,1,0,60,1200,12,1,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1022,'RANDOM_TIP',6,'看三分钟今日热搜','快速扫一眼今天发生了什么，知道就够了，不必一直刷。','<h2>看三分钟今日热搜</h2><p>信息焦虑不是因为知道得太少，而是因为刷得太多。今天给自己三分钟，看一眼热搜就好。</p><h2>怎么看</h2><p>只看榜单标题，不点进去深读，目的是知道大家今天在聊什么。</p><h2>看完之后</h2><p>合上 App，回到手头的事。真正重要的事，会被身边的人反复告诉你的。</p>','随机建议 热搜','https://placehold.co/600x360?text=1022','微博热搜','https://example.com/content/1022','热心网友',1,1,0,62,1300,13,1,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1023,'RANDOM_TIP',6,'收藏一个真正有用的工具','今天只收藏一个工具，并且真的打开试一次。','<h2>收藏一个真正有用的工具</h2><p>收藏夹里躺着一堆没打开过的工具？今天换个方式：只收藏一个，并且立刻试一次。</p><h2>为什么只收藏一个</h2><p>收藏太多等于没收藏，不如挑一个真正用得上的，先跑起来再说。</p><h2>怎么选</h2><p>看你最近最常抱怨的事是什么，找一个能直接解决这件事的工具。</p><h2>试一次</h2><p>不用搞懂所有功能，先用最基础的能力完成一件小事，体验到价值才会继续用。</p>','随机建议 工具','https://placehold.co/600x360?text=1023','少数派','https://example.com/content/1023','热心网友',1,1,0,64,1500,15,1,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1024,'RANDOM_TIP',6,'给未来的自己记一条灵感','一句话就够，不用写完整计划，留个钩子给未来的自己。','<h2>给未来的自己记一条灵感</h2><p>灵感来得快去得也快，不用写完整计划，今天先给未来的自己留一句话就好。</p><h2>记什么</h2><p>任何突然冒出来的想法都行，比如一个选题、一个产品点子、一句想对谁说的话。</p><h2>记在哪</h2><p>随便一个备忘录 App 就行，重点是找起来方便，别折腾工具。</p><h2>之后怎么用</h2><p>过段时间翻一翻，能落地的挑出来做，不能落地的删掉也没关系。</p>','随机建议 灵感','https://placehold.co/600x360?text=1024','知乎热门','https://example.com/content/1024','热心网友',1,1,0,61,1100,11,1,NOW());

INSERT IGNORE INTO content (id,content_type,category_id,title,summary,body,search_keywords,cover_url,source_name,source_url,author_name,status,is_original,is_top,hot_score,view_count,favorite_count,share_count,published_at) VALUES (1025,'RANDOM_TIP',6,'什么都不做，喝口水','休息不是浪费时间，给身体几分钟，它会回报你。','<h2>什么都不做，喝口水</h2><p>有时候最有效的事就是什么都不做。放下手机，喝口水，让大脑喘口气。</p><h2>为什么是水</h2><p>轻微脱水就会让人犯困、注意力下降，喝口水是最便宜的能量补给。</p><h2>怎么休息</h2><p>别刷手机，别想工作，就坐着发会儿呆，或者闭上眼睛两分钟。</p><h2>休息之后</h2><p>你会发现，刚刚卡住的事，常常在休息完的那一刻突然就想通了。</p>','随机建议 休息','https://placehold.co/600x360?text=1025','豆瓣','https://example.com/content/1025','热心网友',1,1,0,65,1700,17,1,NOW());
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1001,1);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1002,1);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1002,4);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1003,1);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1003,6);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1004,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1004,3);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1004,9);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1005,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1005,3);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1006,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1006,3);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1007,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1007,3);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1007,9);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1008,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1008,3);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1009,4);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1009,12);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1010,12);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1011,12);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1012,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1012,4);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1013,5);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1013,6);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1014,6);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1014,7);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1014,12);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1015,3);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1015,5);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1016,7);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1016,10);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1017,7);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1018,7);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1018,11);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1019,6);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1019,8);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1020,6);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1020,8);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1020,12);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1021,8);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1022,1);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1022,8);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1023,4);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1023,12);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1024,2);
INSERT IGNORE INTO content_tag_relation (content_id, tag_id) VALUES (1025,8);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1001,5);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1002,5);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1003,1);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1004,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1005,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1006,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1007,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1008,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1009,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1010,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1011,5);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1012,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1013,1);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1014,1);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1015,2);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1016,1);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1016,3);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1017,3);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1018,4);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1019,1);
INSERT IGNORE INTO content_audience_relation (content_id, audience_id) VALUES (1020,1);

INSERT IGNORE INTO home_recommendation
(id, slot_code, content_id, sort_no, status)
VALUES
(1,'TODAY_RECOMMEND',1001,100,1),
(2,'TODAY_RECOMMEND',1004,90,1),
(3,'TODAY_RECOMMEND',1009,80,1),
(4,'GUESS_LIKE',1013,100,1),
(5,'GUESS_LIKE',1005,90,1);
