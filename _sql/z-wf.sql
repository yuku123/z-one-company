create table if not exists ACT_GE_PROPERTY
(
    NAME_  varchar(64)  not null
    primary key,
    VALUE_ varchar(300) null,
    REV_   int          null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_GE_SCHEMA_LOG
(
    ID_        varchar(64)  not null
    primary key,
    TIMESTAMP_ datetime     null,
    VERSION_   varchar(255) null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_HI_ACTINST
(
    ID_                 varchar(64)  not null
    primary key,
    PARENT_ACT_INST_ID_ varchar(64)  null,
    PROC_DEF_KEY_       varchar(255) null,
    PROC_DEF_ID_        varchar(64)  not null,
    ROOT_PROC_INST_ID_  varchar(64)  null,
    PROC_INST_ID_       varchar(64)  not null,
    EXECUTION_ID_       varchar(64)  not null,
    ACT_ID_             varchar(255) not null,
    TASK_ID_            varchar(64)  null,
    CALL_PROC_INST_ID_  varchar(64)  null,
    CALL_CASE_INST_ID_  varchar(64)  null,
    ACT_NAME_           varchar(255) null,
    ACT_TYPE_           varchar(255) not null,
    ASSIGNEE_           varchar(255) null,
    START_TIME_         datetime     not null,
    END_TIME_           datetime     null,
    DURATION_           bigint       null,
    ACT_INST_STATE_     int          null,
    SEQUENCE_COUNTER_   bigint       null,
    TENANT_ID_          varchar(64)  null,
    REMOVAL_TIME_       datetime     null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_ACTINST_ROOT_PI
    on ACT_HI_ACTINST (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_ACT_INST_COMP
    on ACT_HI_ACTINST (EXECUTION_ID_, ACT_ID_, END_TIME_, ID_);

create index ACT_IDX_HI_ACT_INST_END
    on ACT_HI_ACTINST (END_TIME_);

create index ACT_IDX_HI_ACT_INST_PROCINST
    on ACT_HI_ACTINST (PROC_INST_ID_, ACT_ID_);

create index ACT_IDX_HI_ACT_INST_PROC_DEF_KEY
    on ACT_HI_ACTINST (PROC_DEF_KEY_);

create index ACT_IDX_HI_ACT_INST_RM_TIME
    on ACT_HI_ACTINST (REMOVAL_TIME_);

create index ACT_IDX_HI_ACT_INST_START_END
    on ACT_HI_ACTINST (START_TIME_, END_TIME_);

create index ACT_IDX_HI_ACT_INST_STATS
    on ACT_HI_ACTINST (PROC_DEF_ID_, PROC_INST_ID_, ACT_ID_, END_TIME_, ACT_INST_STATE_);

create index ACT_IDX_HI_ACT_INST_TENANT_ID
    on ACT_HI_ACTINST (TENANT_ID_);

create index ACT_IDX_HI_AI_PDEFID_END_TIME
    on ACT_HI_ACTINST (PROC_DEF_ID_, END_TIME_);

create table if not exists ACT_HI_ATTACHMENT
(
    ID_                varchar(64)   not null
    primary key,
    REV_               int           null,
    USER_ID_           varchar(255)  null,
    NAME_              varchar(255)  null,
    DESCRIPTION_       varchar(4000) null,
    TYPE_              varchar(255)  null,
    TASK_ID_           varchar(64)   null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    URL_               varchar(4000) null,
    CONTENT_ID_        varchar(64)   null,
    TENANT_ID_         varchar(64)   null,
    CREATE_TIME_       datetime      null,
    REMOVAL_TIME_      datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_ATTACHMENT_CONTENT
    on ACT_HI_ATTACHMENT (CONTENT_ID_);

create index ACT_IDX_HI_ATTACHMENT_PROCINST
    on ACT_HI_ATTACHMENT (PROC_INST_ID_);

create index ACT_IDX_HI_ATTACHMENT_RM_TIME
    on ACT_HI_ATTACHMENT (REMOVAL_TIME_);

create index ACT_IDX_HI_ATTACHMENT_ROOT_PI
    on ACT_HI_ATTACHMENT (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_ATTACHMENT_TASK
    on ACT_HI_ATTACHMENT (TASK_ID_);

create table if not exists ACT_HI_BATCH
(
    ID_                  varchar(64)  not null
    primary key,
    TYPE_                varchar(255) null,
    TOTAL_JOBS_          int          null,
    JOBS_PER_SEED_       int          null,
    INVOCATIONS_PER_JOB_ int          null,
    SEED_JOB_DEF_ID_     varchar(64)  null,
    MONITOR_JOB_DEF_ID_  varchar(64)  null,
    BATCH_JOB_DEF_ID_    varchar(64)  null,
    TENANT_ID_           varchar(64)  null,
    CREATE_USER_ID_      varchar(255) null,
    START_TIME_          datetime     not null,
    END_TIME_            datetime     null,
    REMOVAL_TIME_        datetime     null,
    EXEC_START_TIME_     datetime     null
    )
    collate = utf8mb3_bin;

create index ACT_HI_BAT_RM_TIME
    on ACT_HI_BATCH (REMOVAL_TIME_);

create table if not exists ACT_HI_CASEACTINST
(
    ID_                 varchar(64)  not null
    primary key,
    PARENT_ACT_INST_ID_ varchar(64)  null,
    CASE_DEF_ID_        varchar(64)  not null,
    CASE_INST_ID_       varchar(64)  not null,
    CASE_ACT_ID_        varchar(255) not null,
    TASK_ID_            varchar(64)  null,
    CALL_PROC_INST_ID_  varchar(64)  null,
    CALL_CASE_INST_ID_  varchar(64)  null,
    CASE_ACT_NAME_      varchar(255) null,
    CASE_ACT_TYPE_      varchar(255) null,
    CREATE_TIME_        datetime     not null,
    END_TIME_           datetime     null,
    DURATION_           bigint       null,
    STATE_              int          null,
    REQUIRED_           tinyint(1)   null,
    TENANT_ID_          varchar(64)  null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_CAS_A_I_CASEINST
    on ACT_HI_CASEACTINST (CASE_INST_ID_, CASE_ACT_ID_);

create index ACT_IDX_HI_CAS_A_I_COMP
    on ACT_HI_CASEACTINST (CASE_ACT_ID_, END_TIME_, ID_);

create index ACT_IDX_HI_CAS_A_I_CREATE
    on ACT_HI_CASEACTINST (CREATE_TIME_);

create index ACT_IDX_HI_CAS_A_I_END
    on ACT_HI_CASEACTINST (END_TIME_);

create index ACT_IDX_HI_CAS_A_I_TENANT_ID
    on ACT_HI_CASEACTINST (TENANT_ID_);

create table if not exists ACT_HI_CASEINST
(
    ID_                        varchar(64)  not null
    primary key,
    CASE_INST_ID_              varchar(64)  not null,
    BUSINESS_KEY_              varchar(255) null,
    CASE_DEF_ID_               varchar(64)  not null,
    CREATE_TIME_               datetime     not null,
    CLOSE_TIME_                datetime     null,
    DURATION_                  bigint       null,
    STATE_                     int          null,
    CREATE_USER_ID_            varchar(255) null,
    SUPER_CASE_INSTANCE_ID_    varchar(64)  null,
    SUPER_PROCESS_INSTANCE_ID_ varchar(64)  null,
    TENANT_ID_                 varchar(64)  null,
    constraint CASE_INST_ID_
    unique (CASE_INST_ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_CAS_I_BUSKEY
    on ACT_HI_CASEINST (BUSINESS_KEY_);

create index ACT_IDX_HI_CAS_I_CLOSE
    on ACT_HI_CASEINST (CLOSE_TIME_);

create index ACT_IDX_HI_CAS_I_TENANT_ID
    on ACT_HI_CASEINST (TENANT_ID_);

create table if not exists ACT_HI_COMMENT
(
    ID_                varchar(64)   not null
    primary key,
    TYPE_              varchar(255)  null,
    TIME_              datetime      not null,
    USER_ID_           varchar(255)  null,
    TASK_ID_           varchar(64)   null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    ACTION_            varchar(255)  null,
    MESSAGE_           varchar(4000) null,
    FULL_MSG_          longblob      null,
    TENANT_ID_         varchar(64)   null,
    REMOVAL_TIME_      datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_COMMENT_PROCINST
    on ACT_HI_COMMENT (PROC_INST_ID_);

create index ACT_IDX_HI_COMMENT_RM_TIME
    on ACT_HI_COMMENT (REMOVAL_TIME_);

create index ACT_IDX_HI_COMMENT_ROOT_PI
    on ACT_HI_COMMENT (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_COMMENT_TASK
    on ACT_HI_COMMENT (TASK_ID_);

create table if not exists ACT_HI_DECINST
(
    ID_                varchar(64)  not null
    primary key,
    DEC_DEF_ID_        varchar(64)  not null,
    DEC_DEF_KEY_       varchar(255) not null,
    DEC_DEF_NAME_      varchar(255) null,
    PROC_DEF_KEY_      varchar(255) null,
    PROC_DEF_ID_       varchar(64)  null,
    PROC_INST_ID_      varchar(64)  null,
    CASE_DEF_KEY_      varchar(255) null,
    CASE_DEF_ID_       varchar(64)  null,
    CASE_INST_ID_      varchar(64)  null,
    ACT_INST_ID_       varchar(64)  null,
    ACT_ID_            varchar(255) null,
    EVAL_TIME_         datetime     not null,
    REMOVAL_TIME_      datetime     null,
    COLLECT_VALUE_     double       null,
    USER_ID_           varchar(255) null,
    ROOT_DEC_INST_ID_  varchar(64)  null,
    ROOT_PROC_INST_ID_ varchar(64)  null,
    DEC_REQ_ID_        varchar(64)  null,
    DEC_REQ_KEY_       varchar(255) null,
    TENANT_ID_         varchar(64)  null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_DEC_INST_ACT
    on ACT_HI_DECINST (ACT_ID_);

create index ACT_IDX_HI_DEC_INST_ACT_INST
    on ACT_HI_DECINST (ACT_INST_ID_);

create index ACT_IDX_HI_DEC_INST_CI
    on ACT_HI_DECINST (CASE_INST_ID_);

create index ACT_IDX_HI_DEC_INST_ID
    on ACT_HI_DECINST (DEC_DEF_ID_);

create index ACT_IDX_HI_DEC_INST_KEY
    on ACT_HI_DECINST (DEC_DEF_KEY_);

create index ACT_IDX_HI_DEC_INST_PI
    on ACT_HI_DECINST (PROC_INST_ID_);

create index ACT_IDX_HI_DEC_INST_REQ_ID
    on ACT_HI_DECINST (DEC_REQ_ID_);

create index ACT_IDX_HI_DEC_INST_REQ_KEY
    on ACT_HI_DECINST (DEC_REQ_KEY_);

create index ACT_IDX_HI_DEC_INST_RM_TIME
    on ACT_HI_DECINST (REMOVAL_TIME_);

create index ACT_IDX_HI_DEC_INST_ROOT_ID
    on ACT_HI_DECINST (ROOT_DEC_INST_ID_);

create index ACT_IDX_HI_DEC_INST_ROOT_PI
    on ACT_HI_DECINST (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_DEC_INST_TENANT_ID
    on ACT_HI_DECINST (TENANT_ID_);

create index ACT_IDX_HI_DEC_INST_TIME
    on ACT_HI_DECINST (EVAL_TIME_);

create table if not exists ACT_HI_DEC_IN
(
    ID_                varchar(64)   not null
    primary key,
    DEC_INST_ID_       varchar(64)   not null,
    CLAUSE_ID_         varchar(64)   null,
    CLAUSE_NAME_       varchar(255)  null,
    VAR_TYPE_          varchar(100)  null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    TENANT_ID_         varchar(64)   null,
    CREATE_TIME_       datetime      null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    REMOVAL_TIME_      datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_DEC_IN_CLAUSE
    on ACT_HI_DEC_IN (DEC_INST_ID_, CLAUSE_ID_);

create index ACT_IDX_HI_DEC_IN_INST
    on ACT_HI_DEC_IN (DEC_INST_ID_);

create index ACT_IDX_HI_DEC_IN_RM_TIME
    on ACT_HI_DEC_IN (REMOVAL_TIME_);

create index ACT_IDX_HI_DEC_IN_ROOT_PI
    on ACT_HI_DEC_IN (ROOT_PROC_INST_ID_);

create table if not exists ACT_HI_DEC_OUT
(
    ID_                varchar(64)   not null
    primary key,
    DEC_INST_ID_       varchar(64)   not null,
    CLAUSE_ID_         varchar(64)   null,
    CLAUSE_NAME_       varchar(255)  null,
    RULE_ID_           varchar(64)   null,
    RULE_ORDER_        int           null,
    VAR_NAME_          varchar(255)  null,
    VAR_TYPE_          varchar(100)  null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    TENANT_ID_         varchar(64)   null,
    CREATE_TIME_       datetime      null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    REMOVAL_TIME_      datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_DEC_OUT_INST
    on ACT_HI_DEC_OUT (DEC_INST_ID_);

create index ACT_IDX_HI_DEC_OUT_RM_TIME
    on ACT_HI_DEC_OUT (REMOVAL_TIME_);

create index ACT_IDX_HI_DEC_OUT_ROOT_PI
    on ACT_HI_DEC_OUT (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_DEC_OUT_RULE
    on ACT_HI_DEC_OUT (RULE_ORDER_, CLAUSE_ID_);

create table if not exists ACT_HI_DETAIL
(
    ID_                varchar(64)   not null
    primary key,
    TYPE_              varchar(255)  not null,
    PROC_DEF_KEY_      varchar(255)  null,
    PROC_DEF_ID_       varchar(64)   null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    CASE_DEF_KEY_      varchar(255)  null,
    CASE_DEF_ID_       varchar(64)   null,
    CASE_INST_ID_      varchar(64)   null,
    CASE_EXECUTION_ID_ varchar(64)   null,
    TASK_ID_           varchar(64)   null,
    ACT_INST_ID_       varchar(64)   null,
    VAR_INST_ID_       varchar(64)   null,
    NAME_              varchar(255)  not null,
    VAR_TYPE_          varchar(255)  null,
    REV_               int           null,
    TIME_              datetime      not null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    SEQUENCE_COUNTER_  bigint        null,
    TENANT_ID_         varchar(64)   null,
    OPERATION_ID_      varchar(64)   null,
    REMOVAL_TIME_      datetime      null,
    INITIAL_           tinyint(1)    null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_DETAIL_ACT_INST
    on ACT_HI_DETAIL (ACT_INST_ID_);

create index ACT_IDX_HI_DETAIL_BYTEAR
    on ACT_HI_DETAIL (BYTEARRAY_ID_);

create index ACT_IDX_HI_DETAIL_CASE_EXEC
    on ACT_HI_DETAIL (CASE_EXECUTION_ID_);

create index ACT_IDX_HI_DETAIL_CASE_INST
    on ACT_HI_DETAIL (CASE_INST_ID_);

create index ACT_IDX_HI_DETAIL_NAME
    on ACT_HI_DETAIL (NAME_);

create index ACT_IDX_HI_DETAIL_PROC_DEF_KEY
    on ACT_HI_DETAIL (PROC_DEF_KEY_);

create index ACT_IDX_HI_DETAIL_PROC_INST
    on ACT_HI_DETAIL (PROC_INST_ID_);

create index ACT_IDX_HI_DETAIL_RM_TIME
    on ACT_HI_DETAIL (REMOVAL_TIME_);

create index ACT_IDX_HI_DETAIL_ROOT_PI
    on ACT_HI_DETAIL (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_DETAIL_TASK_BYTEAR
    on ACT_HI_DETAIL (BYTEARRAY_ID_, TASK_ID_);

create index ACT_IDX_HI_DETAIL_TASK_ID
    on ACT_HI_DETAIL (TASK_ID_);

create index ACT_IDX_HI_DETAIL_TENANT_ID
    on ACT_HI_DETAIL (TENANT_ID_);

create index ACT_IDX_HI_DETAIL_TIME
    on ACT_HI_DETAIL (TIME_);

create index ACT_IDX_HI_DETAIL_VAR_INST_ID
    on ACT_HI_DETAIL (VAR_INST_ID_);

create table if not exists ACT_HI_EXT_TASK_LOG
(
    ID_                varchar(64)      not null
    primary key,
    TIMESTAMP_         timestamp        not null,
    EXT_TASK_ID_       varchar(64)      not null,
    RETRIES_           int              null,
    TOPIC_NAME_        varchar(255)     null,
    WORKER_ID_         varchar(255)     null,
    PRIORITY_          bigint default 0 not null,
    ERROR_MSG_         varchar(4000)    null,
    ERROR_DETAILS_ID_  varchar(64)      null,
    ACT_ID_            varchar(255)     null,
    ACT_INST_ID_       varchar(64)      null,
    EXECUTION_ID_      varchar(64)      null,
    ROOT_PROC_INST_ID_ varchar(64)      null,
    PROC_INST_ID_      varchar(64)      null,
    PROC_DEF_ID_       varchar(64)      null,
    PROC_DEF_KEY_      varchar(255)     null,
    TENANT_ID_         varchar(64)      null,
    STATE_             int              null,
    REV_               int              null,
    REMOVAL_TIME_      datetime         null
    )
    collate = utf8mb3_bin;

create index ACT_HI_EXT_TASK_LOG_PROCDEF
    on ACT_HI_EXT_TASK_LOG (PROC_DEF_ID_);

create index ACT_HI_EXT_TASK_LOG_PROCINST
    on ACT_HI_EXT_TASK_LOG (PROC_INST_ID_);

create index ACT_HI_EXT_TASK_LOG_PROC_DEF_KEY
    on ACT_HI_EXT_TASK_LOG (PROC_DEF_KEY_);

create index ACT_HI_EXT_TASK_LOG_RM_TIME
    on ACT_HI_EXT_TASK_LOG (REMOVAL_TIME_);

create index ACT_HI_EXT_TASK_LOG_ROOT_PI
    on ACT_HI_EXT_TASK_LOG (ROOT_PROC_INST_ID_);

create index ACT_HI_EXT_TASK_LOG_TENANT_ID
    on ACT_HI_EXT_TASK_LOG (TENANT_ID_);

create index ACT_IDX_HI_EXTTASKLOG_ERRORDET
    on ACT_HI_EXT_TASK_LOG (ERROR_DETAILS_ID_);

create table if not exists ACT_HI_IDENTITYLINK
(
    ID_                varchar(64)  not null
    primary key,
    TIMESTAMP_         timestamp    not null,
    TYPE_              varchar(255) null,
    USER_ID_           varchar(255) null,
    GROUP_ID_          varchar(255) null,
    TASK_ID_           varchar(64)  null,
    ROOT_PROC_INST_ID_ varchar(64)  null,
    PROC_DEF_ID_       varchar(64)  null,
    OPERATION_TYPE_    varchar(64)  null,
    ASSIGNER_ID_       varchar(64)  null,
    PROC_DEF_KEY_      varchar(255) null,
    TENANT_ID_         varchar(64)  null,
    REMOVAL_TIME_      datetime     null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_IDENT_LINK_RM_TIME
    on ACT_HI_IDENTITYLINK (REMOVAL_TIME_);

create index ACT_IDX_HI_IDENT_LINK_TASK
    on ACT_HI_IDENTITYLINK (TASK_ID_);

create index ACT_IDX_HI_IDENT_LNK_GROUP
    on ACT_HI_IDENTITYLINK (GROUP_ID_);

create index ACT_IDX_HI_IDENT_LNK_PROC_DEF_KEY
    on ACT_HI_IDENTITYLINK (PROC_DEF_KEY_);

create index ACT_IDX_HI_IDENT_LNK_ROOT_PI
    on ACT_HI_IDENTITYLINK (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_IDENT_LNK_TENANT_ID
    on ACT_HI_IDENTITYLINK (TENANT_ID_);

create index ACT_IDX_HI_IDENT_LNK_TIMESTAMP
    on ACT_HI_IDENTITYLINK (TIMESTAMP_);

create index ACT_IDX_HI_IDENT_LNK_USER
    on ACT_HI_IDENTITYLINK (USER_ID_);

create table if not exists ACT_HI_INCIDENT
(
    ID_                     varchar(64)   not null
    primary key,
    PROC_DEF_KEY_           varchar(255)  null,
    PROC_DEF_ID_            varchar(64)   null,
    ROOT_PROC_INST_ID_      varchar(64)   null,
    PROC_INST_ID_           varchar(64)   null,
    EXECUTION_ID_           varchar(64)   null,
    CREATE_TIME_            timestamp     not null,
    END_TIME_               timestamp     null,
    INCIDENT_MSG_           varchar(4000) null,
    INCIDENT_TYPE_          varchar(255)  not null,
    ACTIVITY_ID_            varchar(255)  null,
    FAILED_ACTIVITY_ID_     varchar(255)  null,
    CAUSE_INCIDENT_ID_      varchar(64)   null,
    ROOT_CAUSE_INCIDENT_ID_ varchar(64)   null,
    CONFIGURATION_          varchar(255)  null,
    HISTORY_CONFIGURATION_  varchar(255)  null,
    INCIDENT_STATE_         int           null,
    TENANT_ID_              varchar(64)   null,
    JOB_DEF_ID_             varchar(64)   null,
    ANNOTATION_             varchar(4000) null,
    REMOVAL_TIME_           datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_INCIDENT_CREATE_TIME
    on ACT_HI_INCIDENT (CREATE_TIME_);

create index ACT_IDX_HI_INCIDENT_END_TIME
    on ACT_HI_INCIDENT (END_TIME_);

create index ACT_IDX_HI_INCIDENT_PROCINST
    on ACT_HI_INCIDENT (PROC_INST_ID_);

create index ACT_IDX_HI_INCIDENT_PROC_DEF_KEY
    on ACT_HI_INCIDENT (PROC_DEF_KEY_);

create index ACT_IDX_HI_INCIDENT_RM_TIME
    on ACT_HI_INCIDENT (REMOVAL_TIME_);

create index ACT_IDX_HI_INCIDENT_ROOT_PI
    on ACT_HI_INCIDENT (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_INCIDENT_TENANT_ID
    on ACT_HI_INCIDENT (TENANT_ID_);

create table if not exists ACT_HI_JOB_LOG
(
    ID_                     varchar(64)      not null
    primary key,
    TIMESTAMP_              datetime         not null,
    JOB_ID_                 varchar(64)      not null,
    JOB_DUEDATE_            datetime         null,
    JOB_RETRIES_            int              null,
    JOB_PRIORITY_           bigint default 0 not null,
    JOB_EXCEPTION_MSG_      varchar(4000)    null,
    JOB_EXCEPTION_STACK_ID_ varchar(64)      null,
    JOB_STATE_              int              null,
    JOB_DEF_ID_             varchar(64)      null,
    JOB_DEF_TYPE_           varchar(255)     null,
    JOB_DEF_CONFIGURATION_  varchar(255)     null,
    ACT_ID_                 varchar(255)     null,
    FAILED_ACT_ID_          varchar(255)     null,
    EXECUTION_ID_           varchar(64)      null,
    ROOT_PROC_INST_ID_      varchar(64)      null,
    PROCESS_INSTANCE_ID_    varchar(64)      null,
    PROCESS_DEF_ID_         varchar(64)      null,
    PROCESS_DEF_KEY_        varchar(255)     null,
    DEPLOYMENT_ID_          varchar(64)      null,
    SEQUENCE_COUNTER_       bigint           null,
    TENANT_ID_              varchar(64)      null,
    HOSTNAME_               varchar(255)     null,
    REMOVAL_TIME_           datetime         null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_JOB_LOG_EX_STACK
    on ACT_HI_JOB_LOG (JOB_EXCEPTION_STACK_ID_);

create index ACT_IDX_HI_JOB_LOG_JOB_CONF
    on ACT_HI_JOB_LOG (JOB_DEF_CONFIGURATION_);

create index ACT_IDX_HI_JOB_LOG_JOB_DEF_ID
    on ACT_HI_JOB_LOG (JOB_DEF_ID_);

create index ACT_IDX_HI_JOB_LOG_PROCDEF
    on ACT_HI_JOB_LOG (PROCESS_DEF_ID_);

create index ACT_IDX_HI_JOB_LOG_PROCINST
    on ACT_HI_JOB_LOG (PROCESS_INSTANCE_ID_);

create index ACT_IDX_HI_JOB_LOG_PROC_DEF_KEY
    on ACT_HI_JOB_LOG (PROCESS_DEF_KEY_);

create index ACT_IDX_HI_JOB_LOG_RM_TIME
    on ACT_HI_JOB_LOG (REMOVAL_TIME_);

create index ACT_IDX_HI_JOB_LOG_ROOT_PI
    on ACT_HI_JOB_LOG (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_JOB_LOG_TENANT_ID
    on ACT_HI_JOB_LOG (TENANT_ID_);

create table if not exists ACT_HI_OP_LOG
(
    ID_                varchar(64)   not null
    primary key,
    DEPLOYMENT_ID_     varchar(64)   null,
    PROC_DEF_ID_       varchar(64)   null,
    PROC_DEF_KEY_      varchar(255)  null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    CASE_DEF_ID_       varchar(64)   null,
    CASE_INST_ID_      varchar(64)   null,
    CASE_EXECUTION_ID_ varchar(64)   null,
    TASK_ID_           varchar(64)   null,
    JOB_ID_            varchar(64)   null,
    JOB_DEF_ID_        varchar(64)   null,
    BATCH_ID_          varchar(64)   null,
    USER_ID_           varchar(255)  null,
    TIMESTAMP_         timestamp     not null,
    OPERATION_TYPE_    varchar(64)   null,
    OPERATION_ID_      varchar(64)   null,
    ENTITY_TYPE_       varchar(30)   null,
    PROPERTY_          varchar(64)   null,
    ORG_VALUE_         varchar(4000) null,
    NEW_VALUE_         varchar(4000) null,
    TENANT_ID_         varchar(64)   null,
    REMOVAL_TIME_      datetime      null,
    CATEGORY_          varchar(64)   null,
    EXTERNAL_TASK_ID_  varchar(64)   null,
    ANNOTATION_        varchar(4000) null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_OP_LOG_ENTITY_TYPE
    on ACT_HI_OP_LOG (ENTITY_TYPE_);

create index ACT_IDX_HI_OP_LOG_OP_TYPE
    on ACT_HI_OP_LOG (OPERATION_TYPE_);

create index ACT_IDX_HI_OP_LOG_PROCDEF
    on ACT_HI_OP_LOG (PROC_DEF_ID_);

create index ACT_IDX_HI_OP_LOG_PROCINST
    on ACT_HI_OP_LOG (PROC_INST_ID_);

create index ACT_IDX_HI_OP_LOG_RM_TIME
    on ACT_HI_OP_LOG (REMOVAL_TIME_);

create index ACT_IDX_HI_OP_LOG_ROOT_PI
    on ACT_HI_OP_LOG (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_OP_LOG_TASK
    on ACT_HI_OP_LOG (TASK_ID_);

create index ACT_IDX_HI_OP_LOG_TIMESTAMP
    on ACT_HI_OP_LOG (TIMESTAMP_);

create index ACT_IDX_HI_OP_LOG_USER_ID
    on ACT_HI_OP_LOG (USER_ID_);

create table if not exists ACT_HI_PROCINST
(
    ID_                        varchar(64)   not null
    primary key,
    PROC_INST_ID_              varchar(64)   not null,
    BUSINESS_KEY_              varchar(255)  null,
    PROC_DEF_KEY_              varchar(255)  null,
    PROC_DEF_ID_               varchar(64)   not null,
    START_TIME_                datetime      not null,
    END_TIME_                  datetime      null,
    REMOVAL_TIME_              datetime      null,
    DURATION_                  bigint        null,
    START_USER_ID_             varchar(255)  null,
    START_ACT_ID_              varchar(255)  null,
    END_ACT_ID_                varchar(255)  null,
    SUPER_PROCESS_INSTANCE_ID_ varchar(64)   null,
    ROOT_PROC_INST_ID_         varchar(64)   null,
    SUPER_CASE_INSTANCE_ID_    varchar(64)   null,
    CASE_INST_ID_              varchar(64)   null,
    DELETE_REASON_             varchar(4000) null,
    TENANT_ID_                 varchar(64)   null,
    STATE_                     varchar(255)  null,
    constraint PROC_INST_ID_
    unique (PROC_INST_ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_PI_PDEFID_END_TIME
    on ACT_HI_PROCINST (PROC_DEF_ID_, END_TIME_);

create index ACT_IDX_HI_PRO_INST_END
    on ACT_HI_PROCINST (END_TIME_);

create index ACT_IDX_HI_PRO_INST_PROC_DEF_KEY
    on ACT_HI_PROCINST (PROC_DEF_KEY_);

create index ACT_IDX_HI_PRO_INST_PROC_TIME
    on ACT_HI_PROCINST (START_TIME_, END_TIME_);

create index ACT_IDX_HI_PRO_INST_RM_TIME
    on ACT_HI_PROCINST (REMOVAL_TIME_);

create index ACT_IDX_HI_PRO_INST_ROOT_PI
    on ACT_HI_PROCINST (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_PRO_INST_TENANT_ID
    on ACT_HI_PROCINST (TENANT_ID_);

create index ACT_IDX_HI_PRO_I_BUSKEY
    on ACT_HI_PROCINST (BUSINESS_KEY_);

create table if not exists ACT_HI_TASKINST
(
    ID_                varchar(64)   not null
    primary key,
    TASK_DEF_KEY_      varchar(255)  null,
    PROC_DEF_KEY_      varchar(255)  null,
    PROC_DEF_ID_       varchar(64)   null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    CASE_DEF_KEY_      varchar(255)  null,
    CASE_DEF_ID_       varchar(64)   null,
    CASE_INST_ID_      varchar(64)   null,
    CASE_EXECUTION_ID_ varchar(64)   null,
    ACT_INST_ID_       varchar(64)   null,
    NAME_              varchar(255)  null,
    PARENT_TASK_ID_    varchar(64)   null,
    DESCRIPTION_       varchar(4000) null,
    OWNER_             varchar(255)  null,
    ASSIGNEE_          varchar(255)  null,
    START_TIME_        datetime      not null,
    END_TIME_          datetime      null,
    DURATION_          bigint        null,
    DELETE_REASON_     varchar(4000) null,
    PRIORITY_          int           null,
    DUE_DATE_          datetime      null,
    FOLLOW_UP_DATE_    datetime      null,
    TENANT_ID_         varchar(64)   null,
    REMOVAL_TIME_      datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_TASKINSTID_PROCINST
    on ACT_HI_TASKINST (ID_, PROC_INST_ID_);

create index ACT_IDX_HI_TASKINST_PROCINST
    on ACT_HI_TASKINST (PROC_INST_ID_);

create index ACT_IDX_HI_TASKINST_ROOT_PI
    on ACT_HI_TASKINST (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_TASK_INST_END
    on ACT_HI_TASKINST (END_TIME_);

create index ACT_IDX_HI_TASK_INST_PROC_DEF_KEY
    on ACT_HI_TASKINST (PROC_DEF_KEY_);

create index ACT_IDX_HI_TASK_INST_RM_TIME
    on ACT_HI_TASKINST (REMOVAL_TIME_);

create index ACT_IDX_HI_TASK_INST_START
    on ACT_HI_TASKINST (START_TIME_);

create index ACT_IDX_HI_TASK_INST_TENANT_ID
    on ACT_HI_TASKINST (TENANT_ID_);

create table if not exists ACT_HI_VARINST
(
    ID_                varchar(64)   not null
    primary key,
    PROC_DEF_KEY_      varchar(255)  null,
    PROC_DEF_ID_       varchar(64)   null,
    ROOT_PROC_INST_ID_ varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    ACT_INST_ID_       varchar(64)   null,
    CASE_DEF_KEY_      varchar(255)  null,
    CASE_DEF_ID_       varchar(64)   null,
    CASE_INST_ID_      varchar(64)   null,
    CASE_EXECUTION_ID_ varchar(64)   null,
    TASK_ID_           varchar(64)   null,
    NAME_              varchar(255)  not null,
    VAR_TYPE_          varchar(100)  null,
    CREATE_TIME_       datetime      null,
    REV_               int           null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    TENANT_ID_         varchar(64)   null,
    STATE_             varchar(20)   null,
    REMOVAL_TIME_      datetime      null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_HI_CASEVAR_CASE_INST
    on ACT_HI_VARINST (CASE_INST_ID_);

create index ACT_IDX_HI_PROCVAR_NAME_TYPE
    on ACT_HI_VARINST (NAME_, VAR_TYPE_);

create index ACT_IDX_HI_PROCVAR_PROC_INST
    on ACT_HI_VARINST (PROC_INST_ID_);

create index ACT_IDX_HI_VARINST_ACT_INST_ID
    on ACT_HI_VARINST (ACT_INST_ID_);

create index ACT_IDX_HI_VARINST_BYTEAR
    on ACT_HI_VARINST (BYTEARRAY_ID_);

create index ACT_IDX_HI_VARINST_NAME
    on ACT_HI_VARINST (NAME_);

create index ACT_IDX_HI_VARINST_RM_TIME
    on ACT_HI_VARINST (REMOVAL_TIME_);

create index ACT_IDX_HI_VARINST_ROOT_PI
    on ACT_HI_VARINST (ROOT_PROC_INST_ID_);

create index ACT_IDX_HI_VAR_INST_PROC_DEF_KEY
    on ACT_HI_VARINST (PROC_DEF_KEY_);

create index ACT_IDX_HI_VAR_INST_TENANT_ID
    on ACT_HI_VARINST (TENANT_ID_);

create index ACT_IDX_HI_VAR_PI_NAME_TYPE
    on ACT_HI_VARINST (PROC_INST_ID_, NAME_, VAR_TYPE_);

create table if not exists ACT_ID_GROUP
(
    ID_   varchar(64)  not null
    primary key,
    REV_  int          null,
    NAME_ varchar(255) null,
    TYPE_ varchar(255) null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_ID_INFO
(
    ID_        varchar(64)  not null
    primary key,
    REV_       int          null,
    USER_ID_   varchar(64)  null,
    TYPE_      varchar(64)  null,
    KEY_       varchar(255) null,
    VALUE_     varchar(255) null,
    PASSWORD_  longblob     null,
    PARENT_ID_ varchar(255) null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_ID_TENANT
(
    ID_   varchar(64)  not null
    primary key,
    REV_  int          null,
    NAME_ varchar(255) null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_ID_USER
(
    ID_            varchar(64)  not null
    primary key,
    REV_           int          null,
    FIRST_         varchar(255) null,
    LAST_          varchar(255) null,
    EMAIL_         varchar(255) null,
    PWD_           varchar(255) null,
    SALT_          varchar(255) null,
    LOCK_EXP_TIME_ datetime     null,
    ATTEMPTS_      int          null,
    PICTURE_ID_    varchar(64)  null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_ID_MEMBERSHIP
(
    USER_ID_  varchar(64) not null,
    GROUP_ID_ varchar(64) not null,
    primary key (USER_ID_, GROUP_ID_),
    constraint ACT_FK_MEMB_GROUP
    foreign key (GROUP_ID_) references ACT_ID_GROUP (ID_),
    constraint ACT_FK_MEMB_USER
    foreign key (USER_ID_) references ACT_ID_USER (ID_)
    )
    collate = utf8mb3_bin;

create table if not exists ACT_ID_TENANT_MEMBER
(
    ID_        varchar(64) not null
    primary key,
    TENANT_ID_ varchar(64) not null,
    USER_ID_   varchar(64) null,
    GROUP_ID_  varchar(64) null,
    constraint ACT_UNIQ_TENANT_MEMB_GROUP
    unique (TENANT_ID_, GROUP_ID_),
    constraint ACT_UNIQ_TENANT_MEMB_USER
    unique (TENANT_ID_, USER_ID_),
    constraint ACT_FK_TENANT_MEMB
    foreign key (TENANT_ID_) references ACT_ID_TENANT (ID_),
    constraint ACT_FK_TENANT_MEMB_GROUP
    foreign key (GROUP_ID_) references ACT_ID_GROUP (ID_),
    constraint ACT_FK_TENANT_MEMB_USER
    foreign key (USER_ID_) references ACT_ID_USER (ID_)
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RE_CAMFORMDEF
(
    ID_            varchar(64)   not null
    primary key,
    REV_           int           null,
    KEY_           varchar(255)  not null,
    VERSION_       int           not null,
    DEPLOYMENT_ID_ varchar(64)   null,
    RESOURCE_NAME_ varchar(4000) null,
    TENANT_ID_     varchar(64)   null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RE_CASE_DEF
(
    ID_                 varchar(64)   not null
    primary key,
    REV_                int           null,
    CATEGORY_           varchar(255)  null,
    NAME_               varchar(255)  null,
    KEY_                varchar(255)  not null,
    VERSION_            int           not null,
    DEPLOYMENT_ID_      varchar(64)   null,
    RESOURCE_NAME_      varchar(4000) null,
    DGRM_RESOURCE_NAME_ varchar(4000) null,
    TENANT_ID_          varchar(64)   null,
    HISTORY_TTL_        int           null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_CASE_DEF_TENANT_ID
    on ACT_RE_CASE_DEF (TENANT_ID_);

create table if not exists ACT_RE_DECISION_REQ_DEF
(
    ID_                 varchar(64)   not null
    primary key,
    REV_                int           null,
    CATEGORY_           varchar(255)  null,
    NAME_               varchar(255)  null,
    KEY_                varchar(255)  not null,
    VERSION_            int           not null,
    DEPLOYMENT_ID_      varchar(64)   null,
    RESOURCE_NAME_      varchar(4000) null,
    DGRM_RESOURCE_NAME_ varchar(4000) null,
    TENANT_ID_          varchar(64)   null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RE_DECISION_DEF
(
    ID_                 varchar(64)   not null
    primary key,
    REV_                int           null,
    CATEGORY_           varchar(255)  null,
    NAME_               varchar(255)  null,
    KEY_                varchar(255)  not null,
    VERSION_            int           not null,
    DEPLOYMENT_ID_      varchar(64)   null,
    RESOURCE_NAME_      varchar(4000) null,
    DGRM_RESOURCE_NAME_ varchar(4000) null,
    DEC_REQ_ID_         varchar(64)   null,
    DEC_REQ_KEY_        varchar(255)  null,
    TENANT_ID_          varchar(64)   null,
    HISTORY_TTL_        int           null,
    VERSION_TAG_        varchar(64)   null,
    constraint ACT_FK_DEC_REQ
    foreign key (DEC_REQ_ID_) references ACT_RE_DECISION_REQ_DEF (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_DEC_DEF_REQ_ID
    on ACT_RE_DECISION_DEF (DEC_REQ_ID_);

create index ACT_IDX_DEC_DEF_TENANT_ID
    on ACT_RE_DECISION_DEF (TENANT_ID_);

create index ACT_IDX_DEC_REQ_DEF_TENANT_ID
    on ACT_RE_DECISION_REQ_DEF (TENANT_ID_);

create table if not exists ACT_RE_DEPLOYMENT
(
    ID_          varchar(64)  not null
    primary key,
    NAME_        varchar(255) null,
    DEPLOY_TIME_ datetime     null,
    SOURCE_      varchar(255) null,
    TENANT_ID_   varchar(64)  null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_GE_BYTEARRAY
(
    ID_                varchar(64)  not null
    primary key,
    REV_               int          null,
    NAME_              varchar(255) null,
    DEPLOYMENT_ID_     varchar(64)  null,
    BYTES_             longblob     null,
    GENERATED_         tinyint      null,
    TENANT_ID_         varchar(64)  null,
    TYPE_              int          null,
    CREATE_TIME_       datetime     null,
    ROOT_PROC_INST_ID_ varchar(64)  null,
    REMOVAL_TIME_      datetime     null,
    constraint ACT_FK_BYTEARR_DEPL
    foreign key (DEPLOYMENT_ID_) references ACT_RE_DEPLOYMENT (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_BYTEARRAY_NAME
    on ACT_GE_BYTEARRAY (NAME_);

create index ACT_IDX_BYTEARRAY_RM_TIME
    on ACT_GE_BYTEARRAY (REMOVAL_TIME_);

create index ACT_IDX_BYTEARRAY_ROOT_PI
    on ACT_GE_BYTEARRAY (ROOT_PROC_INST_ID_);

create index ACT_IDX_DEPLOYMENT_NAME
    on ACT_RE_DEPLOYMENT (NAME_);

create index ACT_IDX_DEPLOYMENT_TENANT_ID
    on ACT_RE_DEPLOYMENT (TENANT_ID_);

create table if not exists ACT_RE_PROCDEF
(
    ID_                 varchar(64)          not null
    primary key,
    REV_                int                  null,
    CATEGORY_           varchar(255)         null,
    NAME_               varchar(255)         null,
    KEY_                varchar(255)         not null,
    VERSION_            int                  not null,
    DEPLOYMENT_ID_      varchar(64)          null,
    RESOURCE_NAME_      varchar(4000)        null,
    DGRM_RESOURCE_NAME_ varchar(4000)        null,
    HAS_START_FORM_KEY_ tinyint              null,
    SUSPENSION_STATE_   int                  null,
    TENANT_ID_          varchar(64)          null,
    VERSION_TAG_        varchar(64)          null,
    HISTORY_TTL_        int                  null,
    STARTABLE_          tinyint(1) default 1 not null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_PROCDEF_DEPLOYMENT_ID
    on ACT_RE_PROCDEF (DEPLOYMENT_ID_);

create index ACT_IDX_PROCDEF_TENANT_ID
    on ACT_RE_PROCDEF (TENANT_ID_);

create index ACT_IDX_PROCDEF_VER_TAG
    on ACT_RE_PROCDEF (VERSION_TAG_);

create table if not exists ACT_RU_AUTHORIZATION
(
    ID_                varchar(64)  not null
    primary key,
    REV_               int          not null,
    TYPE_              int          not null,
    GROUP_ID_          varchar(255) null,
    USER_ID_           varchar(255) null,
    RESOURCE_TYPE_     int          not null,
    RESOURCE_ID_       varchar(255) null,
    PERMS_             int          null,
    REMOVAL_TIME_      datetime     null,
    ROOT_PROC_INST_ID_ varchar(64)  null,
    constraint ACT_UNIQ_AUTH_GROUP
    unique (GROUP_ID_, TYPE_, RESOURCE_TYPE_, RESOURCE_ID_),
    constraint ACT_UNIQ_AUTH_USER
    unique (USER_ID_, TYPE_, RESOURCE_TYPE_, RESOURCE_ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_AUTH_GROUP_ID
    on ACT_RU_AUTHORIZATION (GROUP_ID_);

create index ACT_IDX_AUTH_RESOURCE_ID
    on ACT_RU_AUTHORIZATION (RESOURCE_ID_);

create index ACT_IDX_AUTH_RM_TIME
    on ACT_RU_AUTHORIZATION (REMOVAL_TIME_);

create index ACT_IDX_AUTH_ROOT_PI
    on ACT_RU_AUTHORIZATION (ROOT_PROC_INST_ID_);

create table if not exists ACT_RU_CASE_EXECUTION
(
    ID_              varchar(64)  not null
    primary key,
    REV_             int          null,
    CASE_INST_ID_    varchar(64)  null,
    SUPER_CASE_EXEC_ varchar(64)  null,
    SUPER_EXEC_      varchar(64)  null,
    BUSINESS_KEY_    varchar(255) null,
    PARENT_ID_       varchar(64)  null,
    CASE_DEF_ID_     varchar(64)  null,
    ACT_ID_          varchar(255) null,
    PREV_STATE_      int          null,
    CURRENT_STATE_   int          null,
    REQUIRED_        tinyint(1)   null,
    TENANT_ID_       varchar(64)  null,
    constraint ACT_FK_CASE_EXE_CASE_DEF
    foreign key (CASE_DEF_ID_) references ACT_RE_CASE_DEF (ID_),
    constraint ACT_FK_CASE_EXE_CASE_INST
    foreign key (CASE_INST_ID_) references ACT_RU_CASE_EXECUTION (ID_)
    on update cascade on delete cascade,
    constraint ACT_FK_CASE_EXE_PARENT
    foreign key (PARENT_ID_) references ACT_RU_CASE_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_CASE_EXEC_BUSKEY
    on ACT_RU_CASE_EXECUTION (BUSINESS_KEY_);

create index ACT_IDX_CASE_EXEC_TENANT_ID
    on ACT_RU_CASE_EXECUTION (TENANT_ID_);

create index ACT_IDX_CASE_EXE_CASE_INST
    on ACT_RU_CASE_EXECUTION (CASE_INST_ID_);

create table if not exists ACT_RU_CASE_SENTRY_PART
(
    ID_                  varchar(64)  not null
    primary key,
    REV_                 int          null,
    CASE_INST_ID_        varchar(64)  null,
    CASE_EXEC_ID_        varchar(64)  null,
    SENTRY_ID_           varchar(255) null,
    TYPE_                varchar(255) null,
    SOURCE_CASE_EXEC_ID_ varchar(64)  null,
    STANDARD_EVENT_      varchar(255) null,
    SOURCE_              varchar(255) null,
    VARIABLE_EVENT_      varchar(255) null,
    VARIABLE_NAME_       varchar(255) null,
    SATISFIED_           tinyint(1)   null,
    TENANT_ID_           varchar(64)  null,
    constraint ACT_FK_CASE_SENTRY_CASE_EXEC
    foreign key (CASE_EXEC_ID_) references ACT_RU_CASE_EXECUTION (ID_),
    constraint ACT_FK_CASE_SENTRY_CASE_INST
    foreign key (CASE_INST_ID_) references ACT_RU_CASE_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RU_EXECUTION
(
    ID_                varchar(64)  not null
    primary key,
    REV_               int          null,
    ROOT_PROC_INST_ID_ varchar(64)  null,
    PROC_INST_ID_      varchar(64)  null,
    BUSINESS_KEY_      varchar(255) null,
    PARENT_ID_         varchar(64)  null,
    PROC_DEF_ID_       varchar(64)  null,
    SUPER_EXEC_        varchar(64)  null,
    SUPER_CASE_EXEC_   varchar(64)  null,
    CASE_INST_ID_      varchar(64)  null,
    ACT_ID_            varchar(255) null,
    ACT_INST_ID_       varchar(64)  null,
    IS_ACTIVE_         tinyint      null,
    IS_CONCURRENT_     tinyint      null,
    IS_SCOPE_          tinyint      null,
    IS_EVENT_SCOPE_    tinyint      null,
    SUSPENSION_STATE_  int          null,
    CACHED_ENT_STATE_  int          null,
    SEQUENCE_COUNTER_  bigint       null,
    TENANT_ID_         varchar(64)  null,
    constraint ACT_FK_EXE_PARENT
    foreign key (PARENT_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_EXE_PROCDEF
    foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_EXE_PROCINST
    foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_)
    on update cascade on delete cascade,
    constraint ACT_FK_EXE_SUPER
    foreign key (SUPER_EXEC_) references ACT_RU_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RU_EVENT_SUBSCR
(
    ID_            varchar(64)  not null
    primary key,
    REV_           int          null,
    EVENT_TYPE_    varchar(255) not null,
    EVENT_NAME_    varchar(255) null,
    EXECUTION_ID_  varchar(64)  null,
    PROC_INST_ID_  varchar(64)  null,
    ACTIVITY_ID_   varchar(255) null,
    CONFIGURATION_ varchar(255) null,
    CREATED_       datetime     not null,
    TENANT_ID_     varchar(64)  null,
    constraint ACT_FK_EVENT_EXEC
    foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_EVENT_SUBSCR_CONFIG_
    on ACT_RU_EVENT_SUBSCR (CONFIGURATION_);

create index ACT_IDX_EVENT_SUBSCR_EVT_NAME
    on ACT_RU_EVENT_SUBSCR (EVENT_NAME_);

create index ACT_IDX_EVENT_SUBSCR_TENANT_ID
    on ACT_RU_EVENT_SUBSCR (TENANT_ID_);

create index ACT_IDX_EXEC_BUSKEY
    on ACT_RU_EXECUTION (BUSINESS_KEY_);

create index ACT_IDX_EXEC_ROOT_PI
    on ACT_RU_EXECUTION (ROOT_PROC_INST_ID_);

create index ACT_IDX_EXEC_TENANT_ID
    on ACT_RU_EXECUTION (TENANT_ID_);

create table if not exists ACT_RU_EXT_TASK
(
    ID_                  varchar(64)      not null
    primary key,
    REV_                 int              not null,
    WORKER_ID_           varchar(255)     null,
    TOPIC_NAME_          varchar(255)     null,
    RETRIES_             int              null,
    ERROR_MSG_           varchar(4000)    null,
    ERROR_DETAILS_ID_    varchar(64)      null,
    LOCK_EXP_TIME_       datetime         null,
    SUSPENSION_STATE_    int              null,
    EXECUTION_ID_        varchar(64)      null,
    PROC_INST_ID_        varchar(64)      null,
    PROC_DEF_ID_         varchar(64)      null,
    PROC_DEF_KEY_        varchar(255)     null,
    ACT_ID_              varchar(255)     null,
    ACT_INST_ID_         varchar(64)      null,
    TENANT_ID_           varchar(64)      null,
    PRIORITY_            bigint default 0 not null,
    LAST_FAILURE_LOG_ID_ varchar(64)      null,
    constraint ACT_FK_EXT_TASK_ERROR_DETAILS
    foreign key (ERROR_DETAILS_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_EXT_TASK_EXE
    foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_EXT_TASK_ERR_DETAILS
    on ACT_RU_EXT_TASK (ERROR_DETAILS_ID_);

create index ACT_IDX_EXT_TASK_EXEC
    on ACT_RU_EXT_TASK (EXECUTION_ID_);

create index ACT_IDX_EXT_TASK_PRIORITY
    on ACT_RU_EXT_TASK (PRIORITY_);

create index ACT_IDX_EXT_TASK_TENANT_ID
    on ACT_RU_EXT_TASK (TENANT_ID_);

create index ACT_IDX_EXT_TASK_TOPIC
    on ACT_RU_EXT_TASK (TOPIC_NAME_);

create table if not exists ACT_RU_FILTER
(
    ID_            varchar(64)  not null
    primary key,
    REV_           int          not null,
    RESOURCE_TYPE_ varchar(255) not null,
    NAME_          varchar(255) not null,
    OWNER_         varchar(255) null,
    QUERY_         longtext     not null,
    PROPERTIES_    longtext     null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RU_JOB
(
    ID_                  varchar(64)      not null
    primary key,
    REV_                 int              null,
    TYPE_                varchar(255)     not null,
    LOCK_EXP_TIME_       datetime         null,
    LOCK_OWNER_          varchar(255)     null,
    EXCLUSIVE_           tinyint(1)       null,
    EXECUTION_ID_        varchar(64)      null,
    PROCESS_INSTANCE_ID_ varchar(64)      null,
    PROCESS_DEF_ID_      varchar(64)      null,
    PROCESS_DEF_KEY_     varchar(255)     null,
    RETRIES_             int              null,
    EXCEPTION_STACK_ID_  varchar(64)      null,
    EXCEPTION_MSG_       varchar(4000)    null,
    FAILED_ACT_ID_       varchar(255)     null,
    DUEDATE_             datetime         null,
    REPEAT_              varchar(255)     null,
    REPEAT_OFFSET_       bigint default 0 null,
    HANDLER_TYPE_        varchar(255)     null,
    HANDLER_CFG_         varchar(4000)    null,
    DEPLOYMENT_ID_       varchar(64)      null,
    SUSPENSION_STATE_    int    default 1 not null,
    JOB_DEF_ID_          varchar(64)      null,
    PRIORITY_            bigint default 0 not null,
    SEQUENCE_COUNTER_    bigint           null,
    TENANT_ID_           varchar(64)      null,
    CREATE_TIME_         datetime         null,
    LAST_FAILURE_LOG_ID_ varchar(64)      null,
    constraint ACT_FK_JOB_EXCEPTION
    foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_JOB_EXECUTION_ID
    on ACT_RU_JOB (EXECUTION_ID_);

create index ACT_IDX_JOB_HANDLER
    on ACT_RU_JOB (HANDLER_TYPE_(100), HANDLER_CFG_(155));

create index ACT_IDX_JOB_HANDLER_TYPE
    on ACT_RU_JOB (HANDLER_TYPE_);

create index ACT_IDX_JOB_JOB_DEF_ID
    on ACT_RU_JOB (JOB_DEF_ID_);

create index ACT_IDX_JOB_PROCINST
    on ACT_RU_JOB (PROCESS_INSTANCE_ID_);

create index ACT_IDX_JOB_TENANT_ID
    on ACT_RU_JOB (TENANT_ID_);

create table if not exists ACT_RU_JOBDEF
(
    ID_                varchar(64)  not null
    primary key,
    REV_               int          null,
    PROC_DEF_ID_       varchar(64)  null,
    PROC_DEF_KEY_      varchar(255) null,
    ACT_ID_            varchar(255) null,
    JOB_TYPE_          varchar(255) not null,
    JOB_CONFIGURATION_ varchar(255) null,
    SUSPENSION_STATE_  int          null,
    JOB_PRIORITY_      bigint       null,
    TENANT_ID_         varchar(64)  null,
    DEPLOYMENT_ID_     varchar(64)  null
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RU_BATCH
(
    ID_                  varchar(64)  not null
    primary key,
    REV_                 int          not null,
    TYPE_                varchar(255) null,
    TOTAL_JOBS_          int          null,
    JOBS_CREATED_        int          null,
    JOBS_PER_SEED_       int          null,
    INVOCATIONS_PER_JOB_ int          null,
    SEED_JOB_DEF_ID_     varchar(64)  null,
    BATCH_JOB_DEF_ID_    varchar(64)  null,
    MONITOR_JOB_DEF_ID_  varchar(64)  null,
    SUSPENSION_STATE_    int          null,
    CONFIGURATION_       varchar(255) null,
    TENANT_ID_           varchar(64)  null,
    CREATE_USER_ID_      varchar(255) null,
    START_TIME_          datetime     null,
    EXEC_START_TIME_     datetime     null,
    constraint ACT_FK_BATCH_JOB_DEF
    foreign key (BATCH_JOB_DEF_ID_) references ACT_RU_JOBDEF (ID_),
    constraint ACT_FK_BATCH_MONITOR_JOB_DEF
    foreign key (MONITOR_JOB_DEF_ID_) references ACT_RU_JOBDEF (ID_),
    constraint ACT_FK_BATCH_SEED_JOB_DEF
    foreign key (SEED_JOB_DEF_ID_) references ACT_RU_JOBDEF (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_BATCH_JOB_DEF
    on ACT_RU_BATCH (BATCH_JOB_DEF_ID_);

create index ACT_IDX_BATCH_MONITOR_JOB_DEF
    on ACT_RU_BATCH (MONITOR_JOB_DEF_ID_);

create index ACT_IDX_BATCH_SEED_JOB_DEF
    on ACT_RU_BATCH (SEED_JOB_DEF_ID_);

create table if not exists ACT_RU_INCIDENT
(
    ID_                     varchar(64)   not null
    primary key,
    REV_                    int           not null,
    INCIDENT_TIMESTAMP_     datetime      not null,
    INCIDENT_MSG_           varchar(4000) null,
    INCIDENT_TYPE_          varchar(255)  not null,
    EXECUTION_ID_           varchar(64)   null,
    ACTIVITY_ID_            varchar(255)  null,
    FAILED_ACTIVITY_ID_     varchar(255)  null,
    PROC_INST_ID_           varchar(64)   null,
    PROC_DEF_ID_            varchar(64)   null,
    CAUSE_INCIDENT_ID_      varchar(64)   null,
    ROOT_CAUSE_INCIDENT_ID_ varchar(64)   null,
    CONFIGURATION_          varchar(255)  null,
    TENANT_ID_              varchar(64)   null,
    JOB_DEF_ID_             varchar(64)   null,
    ANNOTATION_             varchar(4000) null,
    constraint ACT_FK_INC_CAUSE
    foreign key (CAUSE_INCIDENT_ID_) references ACT_RU_INCIDENT (ID_)
    on update cascade on delete cascade,
    constraint ACT_FK_INC_EXE
    foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_INC_JOB_DEF
    foreign key (JOB_DEF_ID_) references ACT_RU_JOBDEF (ID_),
    constraint ACT_FK_INC_PROCDEF
    foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_INC_PROCINST
    foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_INC_RCAUSE
    foreign key (ROOT_CAUSE_INCIDENT_ID_) references ACT_RU_INCIDENT (ID_)
    on update cascade on delete cascade
    )
    collate = utf8mb3_bin;

create index ACT_IDX_INC_CAUSEINCID
    on ACT_RU_INCIDENT (CAUSE_INCIDENT_ID_);

create index ACT_IDX_INC_CONFIGURATION
    on ACT_RU_INCIDENT (CONFIGURATION_);

create index ACT_IDX_INC_EXID
    on ACT_RU_INCIDENT (EXECUTION_ID_);

create index ACT_IDX_INC_JOB_DEF
    on ACT_RU_INCIDENT (JOB_DEF_ID_);

create index ACT_IDX_INC_PROCDEFID
    on ACT_RU_INCIDENT (PROC_DEF_ID_);

create index ACT_IDX_INC_PROCINSTID
    on ACT_RU_INCIDENT (PROC_INST_ID_);

create index ACT_IDX_INC_ROOTCAUSEINCID
    on ACT_RU_INCIDENT (ROOT_CAUSE_INCIDENT_ID_);

create index ACT_IDX_INC_TENANT_ID
    on ACT_RU_INCIDENT (TENANT_ID_);

create index ACT_IDX_JOBDEF_PROC_DEF_ID
    on ACT_RU_JOBDEF (PROC_DEF_ID_);

create index ACT_IDX_JOBDEF_TENANT_ID
    on ACT_RU_JOBDEF (TENANT_ID_);

create table if not exists ACT_RU_METER_LOG
(
    ID_           varchar(64)      not null
    primary key,
    NAME_         varchar(64)      not null,
    REPORTER_     varchar(255)     null,
    VALUE_        bigint           null,
    TIMESTAMP_    datetime         null,
    MILLISECONDS_ bigint default 0 null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_METER_LOG
    on ACT_RU_METER_LOG (NAME_, TIMESTAMP_);

create index ACT_IDX_METER_LOG_MS
    on ACT_RU_METER_LOG (MILLISECONDS_);

create index ACT_IDX_METER_LOG_NAME_MS
    on ACT_RU_METER_LOG (NAME_, MILLISECONDS_);

create index ACT_IDX_METER_LOG_REPORT
    on ACT_RU_METER_LOG (NAME_, REPORTER_, MILLISECONDS_);

create index ACT_IDX_METER_LOG_TIME
    on ACT_RU_METER_LOG (TIMESTAMP_);

create table if not exists ACT_RU_TASK
(
    ID_                varchar(64)   not null
    primary key,
    REV_               int           null,
    EXECUTION_ID_      varchar(64)   null,
    PROC_INST_ID_      varchar(64)   null,
    PROC_DEF_ID_       varchar(64)   null,
    CASE_EXECUTION_ID_ varchar(64)   null,
    CASE_INST_ID_      varchar(64)   null,
    CASE_DEF_ID_       varchar(64)   null,
    NAME_              varchar(255)  null,
    PARENT_TASK_ID_    varchar(64)   null,
    DESCRIPTION_       varchar(4000) null,
    TASK_DEF_KEY_      varchar(255)  null,
    OWNER_             varchar(255)  null,
    ASSIGNEE_          varchar(255)  null,
    DELEGATION_        varchar(64)   null,
    PRIORITY_          int           null,
    CREATE_TIME_       datetime      null,
    LAST_UPDATED_      datetime      null,
    DUE_DATE_          datetime      null,
    FOLLOW_UP_DATE_    datetime      null,
    SUSPENSION_STATE_  int           null,
    TENANT_ID_         varchar(64)   null,
    constraint ACT_FK_TASK_CASE_DEF
    foreign key (CASE_DEF_ID_) references ACT_RE_CASE_DEF (ID_),
    constraint ACT_FK_TASK_CASE_EXE
    foreign key (CASE_EXECUTION_ID_) references ACT_RU_CASE_EXECUTION (ID_),
    constraint ACT_FK_TASK_EXE
    foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_TASK_PROCDEF
    foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_TASK_PROCINST
    foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create table if not exists ACT_RU_IDENTITYLINK
(
    ID_          varchar(64)  not null
    primary key,
    REV_         int          null,
    GROUP_ID_    varchar(255) null,
    TYPE_        varchar(255) null,
    USER_ID_     varchar(255) null,
    TASK_ID_     varchar(64)  null,
    PROC_DEF_ID_ varchar(64)  null,
    TENANT_ID_   varchar(64)  null,
    constraint ACT_FK_ATHRZ_PROCEDEF
    foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_TSKASS_TASK
    foreign key (TASK_ID_) references ACT_RU_TASK (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_ATHRZ_PROCEDEF
    on ACT_RU_IDENTITYLINK (PROC_DEF_ID_);

create index ACT_IDX_IDENT_LNK_GROUP
    on ACT_RU_IDENTITYLINK (GROUP_ID_);

create index ACT_IDX_IDENT_LNK_USER
    on ACT_RU_IDENTITYLINK (USER_ID_);

create index ACT_IDX_TASK_ASSIGNEE
    on ACT_RU_TASK (ASSIGNEE_);

create index ACT_IDX_TASK_CREATE
    on ACT_RU_TASK (CREATE_TIME_);

create index ACT_IDX_TASK_LAST_UPDATED
    on ACT_RU_TASK (LAST_UPDATED_);

create index ACT_IDX_TASK_OWNER
    on ACT_RU_TASK (OWNER_);

create index ACT_IDX_TASK_TENANT_ID
    on ACT_RU_TASK (TENANT_ID_);

create table if not exists ACT_RU_TASK_METER_LOG
(
    ID_            varchar(64) not null
    primary key,
    ASSIGNEE_HASH_ bigint      null,
    TIMESTAMP_     datetime    null
    )
    collate = utf8mb3_bin;

create index ACT_IDX_TASK_METER_LOG_TIME
    on ACT_RU_TASK_METER_LOG (TIMESTAMP_);

create table if not exists ACT_RU_VARIABLE
(
    ID_                  varchar(64)   not null
    primary key,
    REV_                 int           null,
    TYPE_                varchar(255)  not null,
    NAME_                varchar(255)  not null,
    EXECUTION_ID_        varchar(64)   null,
    PROC_INST_ID_        varchar(64)   null,
    PROC_DEF_ID_         varchar(64)   null,
    CASE_EXECUTION_ID_   varchar(64)   null,
    CASE_INST_ID_        varchar(64)   null,
    TASK_ID_             varchar(64)   null,
    BATCH_ID_            varchar(64)   null,
    BYTEARRAY_ID_        varchar(64)   null,
    DOUBLE_              double        null,
    LONG_                bigint        null,
    TEXT_                varchar(4000) null,
    TEXT2_               varchar(4000) null,
    VAR_SCOPE_           varchar(64)   not null,
    SEQUENCE_COUNTER_    bigint        null,
    IS_CONCURRENT_LOCAL_ tinyint       null,
    TENANT_ID_           varchar(64)   null,
    constraint ACT_UNIQ_VARIABLE
    unique (VAR_SCOPE_, NAME_),
    constraint ACT_FK_VAR_BATCH
    foreign key (BATCH_ID_) references ACT_RU_BATCH (ID_),
    constraint ACT_FK_VAR_BYTEARRAY
    foreign key (BYTEARRAY_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_VAR_CASE_EXE
    foreign key (CASE_EXECUTION_ID_) references ACT_RU_CASE_EXECUTION (ID_),
    constraint ACT_FK_VAR_CASE_INST
    foreign key (CASE_INST_ID_) references ACT_RU_CASE_EXECUTION (ID_),
    constraint ACT_FK_VAR_EXE
    foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_VAR_PROCINST
    foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_)
    )
    collate = utf8mb3_bin;

create index ACT_IDX_BATCH_ID
    on ACT_RU_VARIABLE (BATCH_ID_);

create index ACT_IDX_VARIABLE_TASK_ID
    on ACT_RU_VARIABLE (TASK_ID_);

create index ACT_IDX_VARIABLE_TASK_NAME_TYPE
    on ACT_RU_VARIABLE (TASK_ID_, NAME_, TYPE_);

create index ACT_IDX_VARIABLE_TENANT_ID
    on ACT_RU_VARIABLE (TENANT_ID_);

