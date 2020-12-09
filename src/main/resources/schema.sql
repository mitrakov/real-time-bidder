-- sites
CREATE TABLE public.sites (
	id serial NOT NULL,
	exchange_id int8 NOT NULL,
	exchange_site_id int8 NOT NULL,
	"domain" varchar(128) NOT NULL,
	CONSTRAINT sites_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX sites_domain_idx ON public.sites USING btree (domain);

-- sites_placements
CREATE TABLE public.sites_placements (
	id serial NOT NULL,
	site_id int8 NOT NULL,
	tagid varchar(128) NOT NULL,
	CONSTRAINT sites_placements_pkey PRIMARY KEY (id),
	CONSTRAINT sites_placements_site_id_fkey FOREIGN KEY (site_id) REFERENCES sites(id) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE UNIQUE INDEX sites_placements_site_id_idx ON public.sites_placements USING btree (site_id, tagid);

-- segments
CREATE TABLE public.segments (
	id serial NOT NULL,
	site_id int8 NOT NULL,
	placement_id int8 NOT NULL,
	CONSTRAINT segments_pkey PRIMARY KEY (id),
	CONSTRAINT segments_placement_id_fkey FOREIGN KEY (placement_id) REFERENCES sites_placements(id) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT segments_site_id_fkey FOREIGN KEY (site_id) REFERENCES sites(id) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE UNIQUE INDEX segments_site_id_idx ON public.segments USING btree (site_id, placement_id);
