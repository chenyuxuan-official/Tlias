DELETE FROM emp;
DELETE FROM dept;

INSERT INTO dept (id, name, create_time, update_time) VALUES (1, '研发部', '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO dept (id, name, create_time, update_time) VALUES (2, '市场部', '2024-01-01 10:00:00', '2024-01-01 10:00:00');

INSERT INTO emp (id, username, password, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time)
VALUES (1, 'zhangsan', '123456', '张三', 1, '13800138000', 1, 10000, 'a.jpg', '2024-01-01', 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00');

INSERT INTO emp (id, username, password, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time)
VALUES (2, 'lisi', '123456', '李四', 2, '13800138001', 2, 12000, 'b.jpg', '2024-02-01', 2, '2024-02-01 10:00:00', '2024-02-01 10:00:00');
