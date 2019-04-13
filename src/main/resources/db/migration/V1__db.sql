create table entity_with_offset_timestamp
(
    id         int identity (1,1) primary key,
    created_at datetimeoffset
);
