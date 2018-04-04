--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.15
-- Dumped by pg_dump version 9.6.7

SET statement_timeout = 0;
SET lock_timeout = 0;
-- SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
-- SET row_security = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: export_messages; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE export_messages (
    id bigint NOT NULL,
    criticity character varying,
    message_key character varying,
    message_attributes shared_extensions.hstore,
    export_id bigint,
    resource_id bigint,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    resource_attributes shared_extensions.hstore
);


ALTER TABLE export_messages OWNER TO chouette;

--
-- Name: export_messages_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE export_messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE export_messages_id_seq OWNER TO chouette;

--
-- Name: export_messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE export_messages_id_seq OWNED BY export_messages.id;


--
-- Name: export_resources; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE export_resources (
    id bigint NOT NULL,
    export_id bigint,
    status character varying,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    resource_type character varying,
    reference character varying,
    name character varying,
    metrics shared_extensions.hstore
);


ALTER TABLE export_resources OWNER TO chouette;

--
-- Name: export_resources_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE export_resources_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE export_resources_id_seq OWNER TO chouette;

--
-- Name: export_resources_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE export_resources_id_seq OWNED BY export_resources.id;


--
-- Name: exports; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE exports (
    id bigint NOT NULL,
    status character varying,
    current_step_id character varying,
    current_step_progress double precision,
    workbench_id bigint,
    referential_id bigint,
    name character varying,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    file character varying,
    started_at timestamp without time zone,
    ended_at timestamp without time zone,
    token_upload character varying,
    type character varying,
    parent_id bigint,
    parent_type character varying,
    notified_parent_at timestamp without time zone,
    current_step integer DEFAULT 0,
    total_steps integer DEFAULT 0,
    creator character varying,
    options shared_extensions.hstore
);


ALTER TABLE exports OWNER TO chouette;

--
-- Name: exports_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE exports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE exports_id_seq OWNER TO chouette;

--
-- Name: exports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE exports_id_seq OWNED BY exports.id;


--
-- Name: export_messages id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY export_messages ALTER COLUMN id SET DEFAULT nextval('export_messages_id_seq'::regclass);


--
-- Name: export_resources id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY export_resources ALTER COLUMN id SET DEFAULT nextval('export_resources_id_seq'::regclass);


--
-- Name: exports id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY exports ALTER COLUMN id SET DEFAULT nextval('exports_id_seq'::regclass);


--
-- Name: export_messages export_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY export_messages
    ADD CONSTRAINT export_messages_pkey PRIMARY KEY (id);


--
-- Name: export_resources export_resources_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY export_resources
    ADD CONSTRAINT export_resources_pkey PRIMARY KEY (id);


--
-- Name: exports exports_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY exports
    ADD CONSTRAINT exports_pkey PRIMARY KEY (id);


--
-- Name: index_export_messages_on_export_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_export_messages_on_export_id ON export_messages USING btree (export_id);


--
-- Name: index_export_messages_on_resource_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_export_messages_on_resource_id ON export_messages USING btree (resource_id);


--
-- Name: index_export_resources_on_export_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_export_resources_on_export_id ON export_resources USING btree (export_id);


--
-- Name: index_exports_on_referential_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_exports_on_referential_id ON exports USING btree (referential_id);


--
-- Name: index_exports_on_workbench_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_exports_on_workbench_id ON exports USING btree (workbench_id);


--
-- PostgreSQL database dump complete
--

