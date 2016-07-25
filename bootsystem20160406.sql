-- --------------------------------------------------------
-- 主机:                           115.29.151.197
-- 服务器版本:                        5.5.39-MariaDB - MariaDB Server
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  8.2.0.4675
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出 bootsystem 的数据库结构
CREATE DATABASE IF NOT EXISTS `bootsystem` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `bootsystem`;


-- 导出  表 bootsystem.sys_cs 结构
CREATE TABLE IF NOT EXISTS `sys_cs` (
  `sysid` bigint(20) DEFAULT NULL COMMENT '系统参数ID',
  `syscode` varchar(50) DEFAULT NULL COMMENT '系统参数代码',
  `sysnm` varchar(50) DEFAULT NULL COMMENT '系统参数名称',
  `isvalid` int(20) DEFAULT NULL COMMENT '是否启用',
  `sysval` varchar(50) DEFAULT NULL COMMENT '系统参数值',
  `sysexplain` varchar(50) DEFAULT NULL COMMENT '系统参数说明'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_cs 的数据：~3 rows (大约)
/*!40000 ALTER TABLE `sys_cs` DISABLE KEYS */;
INSERT INTO `sys_cs` (`sysid`, `syscode`, `sysnm`, `isvalid`, `sysval`, `sysexplain`) VALUES
	(175091000, '1001', '测试1', 1, '1', ''),
	(175091001, '1002', '测试2', 1, '', ''),
	(175091002, '1003', '测试3', 0, '3', '');
/*!40000 ALTER TABLE `sys_cs` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_dept 结构
CREATE TABLE IF NOT EXISTS `sys_dept` (
  `deptid` bigint(20) NOT NULL,
  `deptnm` varchar(50) DEFAULT NULL,
  `levels` varchar(1000) DEFAULT NULL,
  `parents` bigint(20) DEFAULT NULL,
  `sortno` varchar(10) DEFAULT NULL,
  `isvalid` int(20) DEFAULT NULL,
  `leaf` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`deptid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_dept 的数据：~5 rows (大约)
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` (`deptid`, `deptnm`, `levels`, `parents`, `sortno`, `isvalid`, `leaf`) VALUES
	(1001, '中科信息', NULL, 0, NULL, 1, 'false'),
	(1002, '办公部', NULL, 1001, NULL, 1, 'true'),
	(1003, '软件部', NULL, 1001, NULL, 1, 'false'),
	(1005, '工业部', NULL, 1001, '', 1, 'true'),
	(175091000, '项目部2', NULL, 1003, '', 1, 'true');
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_funcs 结构
CREATE TABLE IF NOT EXISTS `sys_funcs` (
  `funcid` bigint(20) DEFAULT NULL,
  `funcnm` varchar(50) DEFAULT NULL,
  `page` varchar(500) DEFAULT NULL,
  `sortno` varchar(50) DEFAULT NULL,
  `levels` varchar(800) DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL,
  `isvalid` int(20) DEFAULT NULL,
  `parents` bigint(20) DEFAULT NULL,
  `leaf` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_funcs 的数据：~11 rows (大约)
/*!40000 ALTER TABLE `sys_funcs` DISABLE KEYS */;
INSERT INTO `sys_funcs` (`funcid`, `funcnm`, `page`, `sortno`, `levels`, `icon`, `isvalid`, `parents`, `leaf`) VALUES
	(1001, '系统权限管理', '', '1', NULL, 'navicon', 1, 0, 'false'),
	(1002, '基础数据维护', '', '2', NULL, 'cog', 1, 0, 'false'),
	(1003, '系统用户管理', 'sys/sysuser.html', NULL, NULL, NULL, 1, 1001, 'true'),
	(1004, '系统角色管理', 'sys/sysrole.html', NULL, NULL, NULL, 1, 1001, 'true'),
	(1005, '功能菜单管理', 'sys/sysfunc.html', NULL, NULL, NULL, 1, 1001, 'true'),
	(1006, '用户权限管理', 'sys/sysuser_priv.html', NULL, NULL, NULL, 1, 1001, 'true'),
	(1007, '角色权限管理', 'sys/sysrole_priv.html', NULL, NULL, NULL, 1, 1001, 'true'),
	(1008, '用户密码管理', 'sys/password.html', NULL, NULL, NULL, 1, 1001, 'true'),
	(1009, '组织机构管理', 'db/organization.html', NULL, NULL, NULL, 1, 1002, 'true'),
	(1010, '数据字典', 'db/datadic.html', NULL, NULL, NULL, 1, 1002, 'true'),
	(1011, '员工管理', 'db/personnel.html', NULL, NULL, NULL, 1, 1002, 'true');
/*!40000 ALTER TABLE `sys_funcs` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_log 结构
CREATE TABLE IF NOT EXISTS `sys_log` (
  `logid` bigint(20) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `logtype` varchar(50) DEFAULT NULL,
  `log` varchar(500) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`logid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_log 的数据：~193 rows (大约)
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
INSERT INTO `sys_log` (`logid`, `userid`, `logtype`, `log`, `time`) VALUES
	(203591000, 1001, '登录', 'admin登录主页面', '2016-04-01 18:36:45'),
	(203591001, 1001, '登录', 'admin登录主页面', '2016-04-01 18:36:46'),
	(203591002, 1001, '登录', 'admin登录主页面', '2016-04-01 18:36:46'),
	(203591003, 1001, '登录', 'admin登录主页面', '2016-04-05 09:06:51'),
	(203591004, 1001, '登录', 'admin登录主页面', '2016-04-05 09:06:51'),
	(203591005, 1001, '登录', 'admin登录主页面', '2016-04-05 09:06:51'),
	(203591006, 1001, '登录', 'admin登录主页面', '2016-04-05 09:25:42'),
	(203591007, 1001, '登录', 'admin登录主页面', '2016-04-05 09:25:42'),
	(203591008, 1001, '登录', 'admin登录主页面', '2016-04-05 09:25:42'),
	(203591009, 1001, '登录', 'admin登录主页面', '2016-04-05 09:36:18'),
	(203591010, 1001, '登录', 'admin登录主页面', '2016-04-05 09:36:18'),
	(203591011, 1001, '登录', 'admin登录主页面', '2016-04-05 09:36:18'),
	(203591012, 1001, '登录', 'admin登录主页面', '2016-04-05 09:47:36'),
	(203591013, 1001, '登录', 'admin登录主页面', '2016-04-05 09:47:36'),
	(203591014, 1001, '登录', 'admin登录主页面', '2016-04-05 09:47:36'),
	(203591015, 1001, '登录', 'admin登录主页面', '2016-04-05 09:49:19'),
	(203591016, 1001, '登录', 'admin登录主页面', '2016-04-05 09:49:19'),
	(203591017, 1001, '登录', 'admin登录主页面', '2016-04-05 09:49:19'),
	(203591018, 1001, '登录', 'admin登录主页面', '2016-04-05 09:49:24'),
	(203591019, 1001, '登录', 'admin登录主页面', '2016-04-05 09:49:24'),
	(203591020, 1001, '登录', 'admin登录主页面', '2016-04-05 09:49:24'),
	(203591021, 1001, '登录', 'admin登录主页面', '2016-04-05 09:50:57'),
	(203591022, 1001, '登录', 'admin登录主页面', '2016-04-05 09:50:57'),
	(203591023, 1001, '登录', 'admin登录主页面', '2016-04-05 09:50:57'),
	(203591024, 1001, '登录', 'admin登录主页面', '2016-04-05 09:53:20'),
	(203591025, 1001, '登录', 'admin登录主页面', '2016-04-05 09:53:20'),
	(203591026, 1001, '登录', 'admin登录主页面', '2016-04-05 09:53:20'),
	(203591027, 1001, '登录', 'admin登录主页面', '2016-04-05 09:53:31'),
	(203591028, 1001, '登录', 'admin登录主页面', '2016-04-05 09:53:31'),
	(203591029, 1001, '登录', 'admin登录主页面', '2016-04-05 09:53:32'),
	(203591030, 1001, '登录', 'admin登录主页面', '2016-04-05 12:53:44'),
	(203591031, 1001, '登录', 'admin登录主页面', '2016-04-05 12:53:44'),
	(203591032, 1001, '登录', 'admin登录主页面', '2016-04-05 12:53:44'),
	(203591033, 1001, '登录', 'admin登录主页面', '2016-04-05 13:03:59'),
	(203591034, 1001, '登录', 'admin登录主页面', '2016-04-05 13:03:59'),
	(203591035, 1001, '登录', 'admin登录主页面', '2016-04-05 13:03:59'),
	(203591036, 1001, '登录', 'admin登录主页面', '2016-04-05 17:18:22'),
	(203591037, 1001, '登录', 'admin登录主页面', '2016-04-05 17:18:22'),
	(203591038, 1001, '登录', 'admin登录主页面', '2016-04-05 17:18:22'),
	(203591039, 1001, '登录', 'admin登录主页面', '2016-04-06 10:29:10'),
	(203591040, 1001, '登录', 'admin登录主页面', '2016-04-06 10:29:10'),
	(203591041, 1001, '登录', 'admin登录主页面', '2016-04-06 10:29:10'),
	(203591042, 1001, '登录', 'admin登录主页面', '2016-04-06 10:30:29'),
	(203591043, 1001, '登录', 'admin登录主页面', '2016-04-06 10:30:29'),
	(203591044, 1001, '登录', 'admin登录主页面', '2016-04-06 10:30:29'),
	(203591045, 1001, '登录', 'admin登录主页面', '2016-04-06 10:30:38'),
	(203591046, 1001, '登录', 'admin登录主页面', '2016-04-06 10:30:38'),
	(203591047, 1001, '登录', 'admin登录主页面', '2016-04-06 10:30:38'),
	(203591048, 1001, '登录', 'admin登录主页面', '2016-04-06 10:38:16'),
	(203591049, 1001, '登录', 'admin登录主页面', '2016-04-06 10:38:16'),
	(203591050, 1001, '登录', 'admin登录主页面', '2016-04-06 10:38:16'),
	(203591051, 1001, '登录', 'admin登录主页面', '2016-04-06 10:40:23'),
	(203591052, 1001, '登录', 'admin登录主页面', '2016-04-06 10:40:23'),
	(203591053, 1001, '登录', 'admin登录主页面', '2016-04-06 10:40:23'),
	(203691000, 1001, '登录', 'admin登录主页面', '2016-04-01 18:38:25'),
	(203691001, 1001, '登录', 'admin登录主页面', '2016-04-01 18:38:25'),
	(203691002, 1001, '登录', 'admin登录主页面', '2016-04-01 18:38:26'),
	(203791000, 1001, '登录', 'admin登录主页面', '2016-04-05 09:29:21'),
	(203791001, 1001, '登录', 'admin登录主页面', '2016-04-05 09:29:21'),
	(203791002, 1001, '登录', 'admin登录主页面', '2016-04-05 09:29:21'),
	(203791003, 1001, '登录', 'admin登录主页面', '2016-04-05 10:20:15'),
	(203791004, 1001, '登录', 'admin登录主页面', '2016-04-05 10:20:15'),
	(203791005, 1001, '登录', 'admin登录主页面', '2016-04-05 10:20:16'),
	(203791006, 1001, '登录', 'admin登录主页面', '2016-04-05 10:21:58'),
	(203791007, 1001, '登录', 'admin登录主页面', '2016-04-05 10:21:59'),
	(203791008, 1001, '登录', 'admin登录主页面', '2016-04-05 10:21:59'),
	(203791009, 1001, '登录', 'admin登录主页面', '2016-04-05 10:22:46'),
	(203791010, 1001, '登录', 'admin登录主页面', '2016-04-05 10:22:46'),
	(203791011, 1001, '登录', 'admin登录主页面', '2016-04-05 10:22:47'),
	(203791012, 1001, '登录', 'admin登录主页面', '2016-04-05 10:29:28'),
	(203791013, 1001, '登录', 'admin登录主页面', '2016-04-05 10:29:28'),
	(203791014, 1001, '登录', 'admin登录主页面', '2016-04-05 10:29:29'),
	(203791015, 1001, '登录', 'admin登录主页面', '2016-04-05 10:29:50'),
	(203791016, 1001, '登录', 'admin登录主页面', '2016-04-05 10:29:50'),
	(203791017, 1001, '登录', 'admin登录主页面', '2016-04-05 10:29:51'),
	(203791018, 1001, '登录', 'admin登录主页面', '2016-04-05 10:30:06'),
	(203791019, 1001, '登录', 'admin登录主页面', '2016-04-05 10:30:07'),
	(203791020, 1001, '登录', 'admin登录主页面', '2016-04-05 10:30:07'),
	(203791021, 1001, '登录', 'admin登录主页面', '2016-04-05 10:35:12'),
	(203791022, 1001, '登录', 'admin登录主页面', '2016-04-05 10:35:12'),
	(203791023, 1001, '登录', 'admin登录主页面', '2016-04-05 10:35:13'),
	(203791024, 1001, '登录', 'admin登录主页面', '2016-04-05 10:35:20'),
	(203791025, 1001, '登录', 'admin登录主页面', '2016-04-05 10:35:20'),
	(203791026, 1001, '登录', 'admin登录主页面', '2016-04-05 10:35:20'),
	(203791027, 1001, '登录', 'admin登录主页面', '2016-04-05 10:38:07'),
	(203791028, 1001, '登录', 'admin登录主页面', '2016-04-05 10:38:07'),
	(203791029, 1001, '登录', 'admin登录主页面', '2016-04-05 10:38:07'),
	(203791030, 1001, '登录', 'admin登录主页面', '2016-04-05 10:57:36'),
	(203791031, 1001, '登录', 'admin登录主页面', '2016-04-05 10:57:37'),
	(203791032, 1001, '登录', 'admin登录主页面', '2016-04-05 10:57:37'),
	(203791033, 1001, '登录', 'admin登录主页面', '2016-04-05 10:58:16'),
	(203791034, 1001, '登录', 'admin登录主页面', '2016-04-05 10:58:17'),
	(203791035, 1001, '登录', 'admin登录主页面', '2016-04-05 10:58:17'),
	(203791036, 1001, '登录', 'admin登录主页面', '2016-04-05 10:59:50'),
	(203791037, 1001, '登录', 'admin登录主页面', '2016-04-05 10:59:51'),
	(203791038, 1001, '登录', 'admin登录主页面', '2016-04-05 10:59:51'),
	(203791039, 1001, '登录', 'admin登录主页面', '2016-04-05 10:59:56'),
	(203791040, 1001, '登录', 'admin登录主页面', '2016-04-05 10:59:57'),
	(203791041, 1001, '登录', 'admin登录主页面', '2016-04-05 10:59:57'),
	(203791042, 1001, '登录', 'admin登录主页面', '2016-04-05 11:04:49'),
	(203791043, 1001, '登录', 'admin登录主页面', '2016-04-05 11:04:49'),
	(203791044, 1001, '登录', 'admin登录主页面', '2016-04-05 11:04:49'),
	(204091000, 1001, '登录', 'admin登录主页面', '2016-04-05 15:08:44'),
	(204091001, 1001, '登录', 'admin登录主页面', '2016-04-05 15:08:44'),
	(204091002, 1001, '登录', 'admin登录主页面', '2016-04-05 15:08:44'),
	(204091003, 1001, '登录', 'admin登录主页面', '2016-04-05 15:10:14'),
	(204091004, 1001, '登录', 'admin登录主页面', '2016-04-05 15:10:14'),
	(204091005, 1001, '登录', 'admin登录主页面', '2016-04-05 15:10:14'),
	(204191000, 1001, '登录', 'admin登录主页面', '2016-04-05 15:38:07'),
	(204191001, 1001, '登录', 'admin登录主页面', '2016-04-05 15:38:07'),
	(204191002, 1001, '登录', 'admin登录主页面', '2016-04-05 15:38:07'),
	(204191003, 1001, '登录', 'admin登录主页面', '2016-04-05 15:45:54'),
	(204191004, 1001, '登录', 'admin登录主页面', '2016-04-05 15:45:55'),
	(204191005, 1001, '登录', 'admin登录主页面', '2016-04-05 15:45:55'),
	(204191006, 1001, '登录', 'admin登录主页面', '2016-04-05 15:45:59'),
	(204191007, 1001, '登录', 'admin登录主页面', '2016-04-05 15:45:59'),
	(204191008, 1001, '登录', 'admin登录主页面', '2016-04-05 15:45:59'),
	(204191009, 1001, '登录', 'admin登录主页面', '2016-04-05 16:02:28'),
	(204191010, 1001, '登录', 'admin登录主页面', '2016-04-05 16:02:29'),
	(204191011, 1001, '登录', 'admin登录主页面', '2016-04-05 16:02:29'),
	(204191012, 1001, '登录', 'admin登录主页面', '2016-04-05 16:03:25'),
	(204191013, 1001, '登录', 'admin登录主页面', '2016-04-05 16:03:25'),
	(204191014, 1001, '登录', 'admin登录主页面', '2016-04-05 16:03:26'),
	(204191015, 1001, '登录', 'admin登录主页面', '2016-04-05 16:03:30'),
	(204191016, 1001, '登录', 'admin登录主页面', '2016-04-05 16:03:30'),
	(204191017, 1001, '登录', 'admin登录主页面', '2016-04-05 16:03:31'),
	(204191018, 1001, '登录', 'admin登录主页面', '2016-04-05 16:11:56'),
	(204191019, 1001, '登录', 'admin登录主页面', '2016-04-05 16:11:56'),
	(204191020, 1001, '登录', 'admin登录主页面', '2016-04-05 16:11:56'),
	(204191021, 1001, '登录', 'admin登录主页面', '2016-04-05 16:13:02'),
	(204191022, 1001, '登录', 'admin登录主页面', '2016-04-05 16:13:02'),
	(204191023, 1001, '登录', 'admin登录主页面', '2016-04-05 16:13:02'),
	(204191024, 1001, '登录', 'admin登录主页面', '2016-04-05 16:19:29'),
	(204191025, 1001, '登录', 'admin登录主页面', '2016-04-05 16:19:29'),
	(204191026, 1001, '登录', 'admin登录主页面', '2016-04-05 16:19:29'),
	(204191027, 1001, '登录', 'admin登录主页面', '2016-04-05 16:29:41'),
	(204191028, 1001, '登录', 'admin登录主页面', '2016-04-05 16:29:41'),
	(204191029, 1001, '登录', 'admin登录主页面', '2016-04-05 16:29:42'),
	(204191030, 1001, '登录', 'admin登录主页面', '2016-04-05 16:29:53'),
	(204191031, 1001, '登录', 'admin登录主页面', '2016-04-05 16:29:53'),
	(204191032, 1001, '登录', 'admin登录主页面', '2016-04-05 16:29:54'),
	(204191033, 1001, '登录', 'admin登录主页面', '2016-04-05 16:34:08'),
	(204191034, 1001, '登录', 'admin登录主页面', '2016-04-05 16:34:08'),
	(204191035, 1001, '登录', 'admin登录主页面', '2016-04-05 16:34:09'),
	(204191036, 1001, '登录', 'admin登录主页面', '2016-04-05 16:45:23'),
	(204191037, 1001, '登录', 'admin登录主页面', '2016-04-05 16:45:23'),
	(204191038, 1001, '登录', 'admin登录主页面', '2016-04-05 16:45:24'),
	(204191039, 1001, '登录', 'admin登录主页面', '2016-04-05 16:45:32'),
	(204191040, 1001, '登录', 'admin登录主页面', '2016-04-05 16:45:32'),
	(204191041, 1001, '登录', 'admin登录主页面', '2016-04-05 16:45:33'),
	(204191042, 1001, '登录', 'admin登录主页面', '2016-04-05 16:46:51'),
	(204191043, 1001, '登录', 'admin登录主页面', '2016-04-05 16:46:51'),
	(204191044, 1001, '登录', 'admin登录主页面', '2016-04-05 16:46:51'),
	(204191045, 1001, '登录', 'admin登录主页面', '2016-04-05 16:47:02'),
	(204191046, 1001, '登录', 'admin登录主页面', '2016-04-05 16:47:03'),
	(204191047, 1001, '登录', 'admin登录主页面', '2016-04-05 16:47:03'),
	(204191048, 1001, '登录', 'admin登录主页面', '2016-04-05 16:47:07'),
	(204191049, 1001, '登录', 'admin登录主页面', '2016-04-05 16:47:07'),
	(204191050, 1001, '登录', 'admin登录主页面', '2016-04-05 16:47:07'),
	(204191051, 1001, '登录', 'admin登录主页面', '2016-04-05 16:49:05'),
	(204191052, 1001, '登录', 'admin登录主页面', '2016-04-05 16:49:05'),
	(204191053, 1001, '登录', 'admin登录主页面', '2016-04-05 16:49:05'),
	(204191054, 1001, '登录', 'admin登录主页面', '2016-04-05 16:58:50'),
	(204191055, 1001, '登录', 'admin登录主页面', '2016-04-05 16:58:50'),
	(204191056, 1001, '登录', 'admin登录主页面', '2016-04-05 16:58:51'),
	(204191057, 1001, '登录', 'admin登录主页面', '2016-04-05 16:58:54'),
	(204191058, 1001, '登录', 'admin登录主页面', '2016-04-05 16:58:55'),
	(204191059, 1001, '登录', 'admin登录主页面', '2016-04-05 16:58:55'),
	(204191060, 1001, '登录', 'admin登录主页面', '2016-04-05 17:05:12'),
	(204191061, 1001, '登录', 'admin登录主页面', '2016-04-05 17:05:13'),
	(204191062, 1001, '登录', 'admin登录主页面', '2016-04-05 17:05:13'),
	(204191063, 1001, '登录', 'admin登录主页面', '2016-04-05 17:08:52'),
	(204191064, 1001, '登录', 'admin登录主页面', '2016-04-05 17:08:52'),
	(204191065, 1001, '登录', 'admin登录主页面', '2016-04-05 17:08:52'),
	(204191066, 1001, '登录', 'admin登录主页面', '2016-04-05 17:09:58'),
	(204191067, 1001, '登录', 'admin登录主页面', '2016-04-05 17:09:59'),
	(204191068, 1001, '登录', 'admin登录主页面', '2016-04-05 17:09:59'),
	(204191069, 1001, '登录', 'admin登录主页面', '2016-04-05 17:19:11'),
	(204191070, 1001, '登录', 'admin登录主页面', '2016-04-05 17:19:11'),
	(204191071, 1001, '登录', 'admin登录主页面', '2016-04-05 17:19:11'),
	(204191072, 1001, '登录', 'admin登录主页面', '2016-04-05 17:19:31'),
	(204191073, 1001, '登录', 'admin登录主页面', '2016-04-05 17:19:31'),
	(204191074, 1001, '登录', 'admin登录主页面', '2016-04-05 17:19:31'),
	(204191075, 1001, '登录', 'admin登录主页面', '2016-04-05 17:25:24'),
	(204191076, 1001, '登录', 'admin登录主页面', '2016-04-05 17:25:25'),
	(204191077, 1001, '登录', 'admin登录主页面', '2016-04-05 17:25:25'),
	(204191078, 1001, '登录', 'admin登录主页面', '2016-04-05 17:29:40'),
	(204191079, 1001, '登录', 'admin登录主页面', '2016-04-05 17:29:40'),
	(204191080, 1001, '登录', 'admin登录主页面', '2016-04-05 17:29:41'),
	(204191081, 1001, '登录', 'admin登录主页面', '2016-04-05 17:32:35'),
	(204191082, 1001, '登录', 'admin登录主页面', '2016-04-05 17:32:35'),
	(204191083, 1001, '登录', 'admin登录主页面', '2016-04-05 17:32:35'),
	(204191084, 1001, '登录', 'admin登录主页面', '2016-04-05 17:32:39'),
	(204191085, 1001, '登录', 'admin登录主页面', '2016-04-05 17:32:39'),
	(204191086, 1001, '登录', 'admin登录主页面', '2016-04-05 17:32:40'),
	(204191087, 1001, '登录', 'admin登录主页面', '2016-04-05 17:34:35'),
	(204191088, 1001, '登录', 'admin登录主页面', '2016-04-05 17:34:35'),
	(204191089, 1001, '登录', 'admin登录主页面', '2016-04-05 17:34:35'),
	(204191090, 1001, '登录', 'admin登录主页面', '2016-04-05 17:34:38'),
	(204191091, 1001, '登录', 'admin登录主页面', '2016-04-05 17:34:39'),
	(204191092, 1001, '登录', 'admin登录主页面', '2016-04-05 17:34:39'),
	(204191093, 1001, '登录', 'admin登录主页面', '2016-04-05 17:37:04'),
	(204191094, 1001, '登录', 'admin登录主页面', '2016-04-05 17:37:04'),
	(204191095, 1001, '登录', 'admin登录主页面', '2016-04-05 17:37:04'),
	(204191096, 1001, '登录', 'admin登录主页面', '2016-04-05 17:40:54'),
	(204191097, 1001, '登录', 'admin登录主页面', '2016-04-05 17:40:54'),
	(204191098, 1001, '登录', 'admin登录主页面', '2016-04-05 17:40:54'),
	(204191099, 1001, '保存数据', '保存系统用户权限信息', '2016-04-05 17:41:37'),
	(204291000, 1001, '登录', 'admin登录主页面', '2016-04-06 09:25:53'),
	(204291001, 1001, '登录', 'admin登录主页面', '2016-04-06 09:25:53'),
	(204291002, 1001, '登录', 'admin登录主页面', '2016-04-06 09:25:54'),
	(204291003, 1001, '登录', 'admin登录主页面', '2016-04-06 09:42:51'),
	(204291004, 1001, '登录', 'admin登录主页面', '2016-04-06 09:42:51'),
	(204291005, 1001, '登录', 'admin登录主页面', '2016-04-06 09:42:52'),
	(204291006, 1001, '登录', 'admin登录主页面', '2016-04-06 09:43:03'),
	(204291007, 1001, '登录', 'admin登录主页面', '2016-04-06 09:43:03'),
	(204291008, 1001, '登录', 'admin登录主页面', '2016-04-06 09:43:03'),
	(204291009, 1001, '登录', 'admin登录主页面', '2016-04-06 09:47:07'),
	(204291010, 1001, '登录', 'admin登录主页面', '2016-04-06 09:47:07'),
	(204291011, 1001, '登录', 'admin登录主页面', '2016-04-06 09:47:07'),
	(204391000, 1001, '登录', 'admin登录主页面', '2016-04-06 10:13:36'),
	(204391001, 1001, '登录', 'admin登录主页面', '2016-04-06 10:13:36'),
	(204391002, 1001, '登录', 'admin登录主页面', '2016-04-06 10:13:36'),
	(204391003, 1001, '登录', 'admin登录主页面', '2016-04-06 10:13:41'),
	(204391004, 1001, '登录', 'admin登录主页面', '2016-04-06 10:13:41'),
	(204391005, 1001, '登录', 'admin登录主页面', '2016-04-06 10:13:41'),
	(204391006, 1001, '登录', 'admin登录主页面', '2016-04-06 10:35:50'),
	(204391007, 1001, '登录', 'admin登录主页面', '2016-04-06 10:35:50'),
	(204391008, 1001, '登录', 'admin登录主页面', '2016-04-06 10:35:50'),
	(204491000, 1001, '登录', 'admin登录主页面', '2016-04-06 10:40:01'),
	(204491001, 1001, '登录', 'admin登录主页面', '2016-04-06 10:40:01'),
	(204491002, 1001, '登录', 'admin登录主页面', '2016-04-06 10:40:01');
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_role 结构
CREATE TABLE IF NOT EXISTS `sys_role` (
  `roleid` bigint(20) DEFAULT NULL,
  `rolename` varchar(50) DEFAULT NULL,
  `rolecode` varchar(50) DEFAULT NULL,
  `beizhu` varchar(300) DEFAULT NULL,
  `sortnouser` int(20) DEFAULT NULL,
  `isvalid` int(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_role 的数据：~4 rows (大约)
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` (`roleid`, `rolename`, `rolecode`, `beizhu`, `sortnouser`, `isvalid`) VALUES
	(3001, '系统管理员', '3001', NULL, NULL, 1),
	(3002, '测试角色', '3002', NULL, NULL, 1),
	(3003, '角色1', '3003', NULL, NULL, 1),
	(3004, '角色2', '3004', NULL, NULL, 1);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_role_func 结构
CREATE TABLE IF NOT EXISTS `sys_role_func` (
  `rolefunid` bigint(20) DEFAULT NULL,
  `roleid` bigint(20) DEFAULT NULL,
  `funcid` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_role_func 的数据：~11 rows (大约)
/*!40000 ALTER TABLE `sys_role_func` DISABLE KEYS */;
INSERT INTO `sys_role_func` (`rolefunid`, `roleid`, `funcid`) VALUES
	(175091000, 3001, 1001),
	(175091001, 3001, 1003),
	(175091002, 3001, 1004),
	(175091003, 3001, 1005),
	(175091004, 3001, 1006),
	(175091005, 3001, 1007),
	(175091006, 3001, 1008),
	(175091007, 3004, 1002),
	(175091008, 3004, 1009),
	(175091009, 3004, 1010),
	(175091010, 3004, 1011);
/*!40000 ALTER TABLE `sys_role_func` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_tablepk 结构
CREATE TABLE IF NOT EXISTS `sys_tablepk` (
  `tablenm` varchar(50) DEFAULT NULL,
  `currentpk` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_tablepk 的数据：~8 rows (大约)
/*!40000 ALTER TABLE `sys_tablepk` DISABLE KEYS */;
INSERT INTO `sys_tablepk` (`tablenm`, `currentpk`) VALUES
	('sys_log', 1061),
	('sys_user', 1003),
	('sys_role', 1001),
	('sys_funcs', 1004),
	('sys_user_func', 1002),
	('sys_role_func', 1000),
	('sys_dept', 1002),
	('sys_cs', 1000);
/*!40000 ALTER TABLE `sys_tablepk` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_user 结构
CREATE TABLE IF NOT EXISTS `sys_user` (
  `userid` bigint(20) NOT NULL,
  `usernm` varchar(50) DEFAULT NULL,
  `loginnm` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `phone` varchar(80) DEFAULT NULL,
  `deptidf` bigint(20) DEFAULT NULL,
  `roleidf` bigint(20) DEFAULT NULL,
  `memo` varchar(800) DEFAULT NULL,
  `sortnouser` int(20) DEFAULT NULL,
  `isvalid` int(20) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_user 的数据：~5 rows (大约)
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` (`userid`, `usernm`, `loginnm`, `password`, `phone`, `deptidf`, `roleidf`, `memo`, `sortnouser`, `isvalid`) VALUES
	(1001, '管理员', 'admin', '670B14728AD9902AECBA32E22FA4F6BD', '13423450987', 1001, 3001, '', NULL, 1),
	(1002, '测试', 'ces', '670B14728AD9902AECBA32E22FA4F6BD', '18290023408', 1005, 3002, NULL, NULL, 1),
	(1003, '角色1', 'jues1', '670B14728AD9902AECBA32E22FA4F6BD', '18729345609', 1002, 3002, '1', NULL, 1),
	(1004, '角色2', 'jues2', '670B14728AD9902AECBA32E22FA4F6BD', NULL, 175091000, 3003, NULL, NULL, 1),
	(1005, '测试2', 'ces2', '670B14728AD9902AECBA32E22FA4F6BD', NULL, 1003, 3004, NULL, NULL, 1);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;


-- 导出  表 bootsystem.sys_user_func 结构
CREATE TABLE IF NOT EXISTS `sys_user_func` (
  `userfuncid` bigint(20) DEFAULT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `funcid` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  bootsystem.sys_user_func 的数据：~26 rows (大约)
/*!40000 ALTER TABLE `sys_user_func` DISABLE KEYS */;
INSERT INTO `sys_user_func` (`userfuncid`, `userid`, `funcid`) VALUES
	(10041, 1003, 1001),
	(10042, 1003, 1004),
	(10043, 1003, 1006),
	(10051, 1004, 1002),
	(10052, 1004, 1009),
	(175091028, 1002, 1001),
	(175091029, 1002, 1003),
	(175091030, 1002, 1004),
	(175091031, 1002, 1005),
	(175091032, 1002, 1006),
	(175091033, 1002, 1007),
	(175091034, 1002, 1008),
	(175091035, 1002, 1002),
	(175091036, 1002, 1009),
	(175091037, 1002, 1010),
	(175091038, 1002, 1011),
	(175191000, 1001, 1001),
	(175191001, 1001, 1003),
	(175191002, 1001, 1005),
	(175191003, 1001, 1006),
	(175191004, 1001, 1007),
	(175191005, 1001, 1008),
	(175191006, 1001, 1002),
	(175191007, 1001, 1009),
	(175191008, 1001, 1010),
	(175191009, 1001, 1011);
/*!40000 ALTER TABLE `sys_user_func` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
