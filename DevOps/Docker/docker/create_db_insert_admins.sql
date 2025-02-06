create database travel_agency_web_db;

use travel_agency_web_db;

create table additional_user_info (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        document_path varchar(255) not null,
        user_id bigint,
        primary key (id)
    );
    
create table offers (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        city varchar(255) not null,
        country varchar(255) not null,
        creation_date datetime(6),
        description TEXT not null,
        discount float(53),
        duration_in_days integer not null,
        price decimal(38,2) not null,
        stars float(53) not null,
        title varchar(255),
        transport_type varchar(255) not null,
        user_id bigint,
        primary key (id)
    );
    
create table offices (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        address varchar(255) not null,
        city varchar(255) not null,
        country varchar(255) not null,
        phone varchar(255) not null,
        user_id bigint,
        primary key (id)
    );
    
create table roles (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        role_name varchar(255),
        primary key (id)
    );
    
create table tokens (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        expired bit not null,
        revoked bit not null,
        token varchar(255),
        token_type varchar(255),
        user_id bigint,
        primary key (id)
    );
    
 create table tour_offer_file (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        picture_uri varchar(255) not null,
        offer_id bigint not null,
        primary key (id)
    );   
    
create table users (
       id bigint not null auto_increment,
        is_deleted boolean default false,
        approved_by integer,
        email_address varchar(255) not null,
        first_name varchar(255) not null,
        has_star boolean default false,
        last_name varchar(255) not null,
        password varchar(255) not null,
        phone_number varchar(255) not null,
        role_id bigint,
        primary key (id)
    );

alter table additional_user_info 
       add constraint FKadditional_user_info_user_id 
       foreign key (user_id) 
       references users (id);
    
alter table offers 
       add constraint FKoffers_user_id
       foreign key (user_id) 
       references users (id);
       
alter table offices 
       add constraint FKoffices_user_id
       foreign key (user_id) 
       references users (id);
       
alter table tokens 
       add constraint FKtokens_user_id
       foreign key (user_id) 
       references users (id);
       
alter table tour_offer_file 
       add constraint FKtour_offer_file_offer_id
       foreign key (offer_id) 
       references offers (id);
       
alter table users 
       add constraint FKusers_role_id
       foreign key (role_id) 
       references roles (id);
       
#Creating DB indexes and DB table fixes

 CREATE INDEX country_index ON offers(country);
 CREATE INDEX city_index ON offers(city);
 CREATE INDEX duration_index ON offers(duration_in_days);
 CREATE INDEX stars_index ON offers(stars);
 CREATE INDEX price_index ON offers(price);
 CREATE INDEX discount_index ON offers(discount);
 CREATE INDEX user_id_index ON offers(user_id);
 
 #insert roles
 insert into roles (role_name) values('admin'),('inactive'),('active');
 
#create default admin user/acc:admin1@abv.bg pass:admin321
#create default admin user/acc:admin2@abv.bg pass:admin987
insert into
    users(approved_by,
          email_address,
          first_name,
          last_name,
          password,
          phone_number,
          role_id)
values
       (0,
        'admin1@abv.bg',
        'admin1',
        'admin1',
        '$2a$10$aveTbx.OD8l0ygH4ILMrCuIISac1QuO1EMyKLHLW9AwD4NupnV4uC',
        '0884661663',
        1),
       (0,
        'admin2@abv.bg',
        'admin2',
        'admin2',
        '$2a$10$x4aCbHWbHdm01o9QdC9moeA14aLjnECilNtb9TSQRNTUuME2TPBSm',
        '0884600585',
        1);