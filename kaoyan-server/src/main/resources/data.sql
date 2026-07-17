MERGE INTO subject (code, name) KEY (code) VALUES ('english', '英语一');
MERGE INTO subject (code, name) KEY (code) VALUES ('math', '数学一');
MERGE INTO subject (code, name) KEY (code) VALUES ('politics', '政治');
MERGE INTO subject (code, name) KEY (code) VALUES ('cs408', '408');
MERGE INTO subject (code, name) KEY (code) VALUES ('ds', '数据结构');
MERGE INTO subject (code, name) KEY (code) VALUES ('co', '计算机组成原理');
MERGE INTO subject (code, name) KEY (code) VALUES ('os', '操作系统');
MERGE INTO subject (code, name) KEY (code) VALUES ('cn', '计算机网络');

-- 408 知识图谱：数据结构
MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds', '数据结构', '数据结构核心知识', s.id, NULL, 1, 10, 0, CURRENT_DATE + 1, 0 FROM subject s WHERE s.code = 'ds';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-linear', '线性表', '数组、链表、栈、队列', s.id, n.id, 2, 9, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-array', '数组', '顺序存储、随机访问', s.id, n.id, 3, 7, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds-linear';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-linkedlist', '链表', '单链表、双链表、循环链表', s.id, n.id, 3, 8, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds-linear';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-stack', '栈', '后进先出、表达式求值', s.id, n.id, 3, 7, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds-linear';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-queue', '队列', '先进先出、双端队列', s.id, n.id, 3, 7, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds-linear';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-tree', '树与二叉树', '二叉树、BST、AVL、B树、堆', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-graph', '图', '存储、遍历、最短路径、最小生成树', s.id, n.id, 2, 9, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-sort', '排序', '内部排序算法对比', s.id, n.id, 2, 9, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'ds-search', '查找', '顺序、折半、哈希、B树查找', s.id, n.id, 2, 8, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'ds' AND n.code = 'ds';

-- 408 知识图谱：计算机组成原理
MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co', '计算机组成原理', '计组核心知识', s.id, NULL, 1, 10, 0, CURRENT_DATE + 1, 0 FROM subject s WHERE s.code = 'co';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co-data', '数据的表示和运算', '定点数、浮点数、ALU', s.id, n.id, 2, 9, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'co' AND n.code = 'co';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co-memory', '存储器层次', 'Cache、主存、虚拟存储', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'co' AND n.code = 'co';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co-instruction', '指令系统', '指令格式、寻址方式、CISC/RISC', s.id, n.id, 2, 8, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'co' AND n.code = 'co';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co-cpu', 'CPU', '数据通路、控制器、流水线', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'co' AND n.code = 'co';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co-bus', '总线', '总线仲裁、总线标准', s.id, n.id, 2, 6, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'co' AND n.code = 'co';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'co-io', 'I/O系统', 'I/O方式、中断、DMA', s.id, n.id, 2, 8, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'co' AND n.code = 'co';

-- 408 知识图谱：操作系统
MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'os', '操作系统', '操作系统核心知识', s.id, NULL, 1, 10, 0, CURRENT_DATE + 1, 0 FROM subject s WHERE s.code = 'os';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'os-process', '进程管理', '进程、线程、调度、同步互斥、死锁', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'os' AND n.code = 'os';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'os-memory', '内存管理', '分页分段、虚拟内存、页面置换', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'os' AND n.code = 'os';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'os-file', '文件系统', '文件逻辑结构、目录、磁盘管理', s.id, n.id, 2, 8, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'os' AND n.code = 'os';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'os-io', 'I/O管理', 'I/O控制方式、缓冲、SPOOLing', s.id, n.id, 2, 7, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'os' AND n.code = 'os';

-- 408 知识图谱：计算机网络
MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'cn', '计算机网络', '计网核心知识', s.id, NULL, 1, 10, 0, CURRENT_DATE + 1, 0 FROM subject s WHERE s.code = 'cn';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'cn-physical', '物理层', '奈氏准则、香农定理、编码调制', s.id, n.id, 2, 7, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'cn' AND n.code = 'cn';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'cn-datalink', '数据链路层', '封装成帧、差错控制、介质访问控制', s.id, n.id, 2, 9, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'cn' AND n.code = 'cn';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'cn-network', '网络层', 'IP、路由、NAT、ICMP', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'cn' AND n.code = 'cn';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'cn-transport', '传输层', 'TCP、UDP、拥塞控制', s.id, n.id, 2, 10, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'cn' AND n.code = 'cn';

MERGE INTO knowledge_node (code, name, description, subject_id, parent_id, level, weight, stage, next_review_date, mastery_rate) KEY (code)
SELECT 'cn-application', '应用层', 'HTTP、DNS、FTP、邮件', s.id, n.id, 2, 8, 0, CURRENT_DATE + 1, 0 FROM subject s, knowledge_node n WHERE s.code = 'cn' AND n.code = 'cn';

-- 知识点依赖关系（前置）
MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'ds-array' AND t.code = 'ds-linkedlist';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'ds-linear' AND t.code = 'ds-tree';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'ds-tree' AND t.code = 'ds-graph';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'ds-search' AND t.code = 'ds-sort';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'co-data' AND t.code = 'co-cpu';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'co-memory' AND t.code = 'co-cpu';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'os-process' AND t.code = 'os-memory';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'cn-network' AND t.code = 'cn-transport';

MERGE INTO knowledge_edge (source_id, target_id, relation_type) KEY (source_id, target_id)
SELECT s.id, t.id, 'prerequisite' FROM knowledge_node s, knowledge_node t WHERE s.code = 'cn-transport' AND t.code = 'cn-application';
