-- 记住我功能用的表
create table persistent_logins (username varchar(64) not null,
								series varchar(64) primary key,
								token varchar(64) not null,
								last_used timestamp not null);
-- 社交登录用的表
-- This SQL contains a "create table" that can be used to create a table that JdbcUsersConnectionRepository can persist
-- connection in. It is, however, not to be assumed to be production-ready, all-purpose SQL. It is merely representative
-- of the kind of table that JdbcUsersConnectionRepository works with. The table and column names, as well as the general
-- column types, are what is important. Specific column types and sizes that work may vary across database vendors and
-- the required sizes may vary across API providers.
create table zhanlu_UserConnection (
--业务系统userid
    userId varchar(255) not null,
	-- 服务提供商id  qq 还是 weixin
	providerId varchar(255) not null,
	-- 服务提供商中的opendid
	providerUserId varchar(255),
	rank int not null,
	displayName varchar(255),
	profileUrl varchar(512),
	imageUrl varchar(512),
	accessToken varchar(512) not null,
	secret varchar(512),
	refreshToken varchar(512),
	expireTime bigint,
	primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on imooc_UserConnection(userId, providerId, rank);
-- rank  替换成 'rank' 不然mysql会关键字报错哦（某些版本）
create table zhanlu_UserConnection (userId varchar(255) not null,
	providerId varchar(255) not null,
	providerUserId varchar(255),
	'rank' int not null,
	displayName varchar(255),
	profileUrl varchar(512),
	imageUrl varchar(512),
	accessToken varchar(512) not null,
	secret varchar(512),
	refreshToken varchar(512),
	expireTime bigint,
	primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on imooc_UserConnection(userId, providerId, 'rank');