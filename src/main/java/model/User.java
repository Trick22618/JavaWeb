package model;

import lombok.Data;

// 使用者資料
/**
 * use web;
create table if not exists user (
	id int auto_increment primary key,
    username varchar(50) not null unique,
    hash varchar(255) not null,
    salt varchar(255)not null,
    priority int default 0
);

-- 密碼: 1234
insert into user(username, hash, salt) value('admin', '7d2cdd1682a401c095b47f9d697a01282316fe497ef261c8abdfa4d56c3e167a', '8Y7jpF84K6BphAre+379zQ==', 1); 
-- 密碼: 5678
insert into user(username, hash, salt) value('john', 'f74ac2491ebf53fd3fb0d403f4aae703784098a45581b3ed6262fd2fbbf59eae', 'kaG0aeaQB4lldBwyhrgwaQ==', 0); 
-- 密碼: 1234
insert into user(username, hash, salt) value('mary', '2d93a6a3ac82850c9368ab49a1d5e2664b563285e97ce44cd31a78caf14dd0c3', 'OwMG2U3DtGGvIT5ZRc8D2Q==', 0); 

 */
@Data
public class User {
	private Integer id;
	private String username;
	private String hash;
	private String salt;
	private Integer priority;
	
}
