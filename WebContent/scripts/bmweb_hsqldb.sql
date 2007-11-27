/* Script para borrar, crear y poblar base de datos HSQLDB */
/* Denis - 2005.10.17 */

/*************************************************************************
	DOMINIOS (CIUDADES, ETC)
*************************************************************************/
drop table keyword_det if exists;

create table keyword_det (
  key_id integer,
  key_descr varchar(100),
  key_sist varchar(100),
  key_word varchar(100)
)

/*** CIUDADES ***/

insert into keyword_det(key_id, key_descr, key_sist, key_word) values 
(1, 'Santiago', 'BENMED', 'CIUDAD');
insert into keyword_det(key_id, key_descr, key_sist, key_word) values 
(2, 'Concepcion', 'BENMED', 'CIUDAD');
insert into keyword_det(key_id, key_descr, key_sist, key_word) values 
(3, 'Antofagasta', 'BENMED', 'CIUDAD');

/*** PRESTACIONES GENERICAS ***/

insert into keyword_det(key_id, key_descr, key_sist, key_word) values 
(1, 'Consulta Generica', 'BENMED', 'PREGEN');

insert into keyword_det(key_id, key_descr, key_sist, key_word) values 
(2, 'Oftalmologia', 'BENMED', 'PREGEN');

insert into keyword_det(key_id, key_descr, key_sist, key_word) values 
(3, 'Traumatologia', 'BENMED', 'PREGEN');


/*************************************************************************
	HABILITADOS
*************************************************************************/

drop table bm_habilitado if exists;

create table bm_habilitado (
  ha_codigo integer,
  ha_nombre varchar(100),
  ha_ubicacion varchar(100),
  dom_ciudad int,
  ha_direccion varchar(100),
  ha_responsable varchar(100),
  ha_activo varchar(1)
);

insert into bm_habilitado (
 ha_codigo, ha_nombre, ha_ubicacion, dom_ciudad, ha_direccion,
 ha_responsable, ha_activo ) values (
 1, 'Denis 1','ubicacion1',1,'d1','resp1','s');
 
insert into bm_habilitado (
 ha_codigo, ha_nombre, ha_ubicacion, dom_ciudad, ha_direccion,
 ha_responsable, ha_activo ) values (
 2, 'Denis 2','ubicacion 2',1,'d2','resp2','s');
 
insert into bm_habilitado (
 ha_codigo, ha_nombre, ha_ubicacion, dom_ciudad, ha_direccion,
 ha_responsable, ha_activo ) values (
 3, 'Denis 3','ubicacion3',2,'d3','resp3','n');
 
/*************************************************************************
	BONOS
*************************************************************************/

drop table bm_bono if exists;

create table bm_bono (
  bo_serial integer identity,
  dom_tipbon varchar(1),
  bo_folio integer,
  pb_rut varchar(100),
  be_carne varchar(100),
  bo_fecemi timestamp,
  dom_ciudad integer,
  ha_codigo integer,
  bo_rutimp varchar(100),
  bo_nroate integer,
  dp_serial integer,
  bm_liquida integer,
  dom_cauext integer,
  dom_deriva integer,
  dp_origen varchar(100),
  dom_estbon varchar(100)
 );

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999121, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999122, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);
 
insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999123, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999124, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999125, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999126, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999127, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999128, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999129, '12345678-9', '12345678-9', '2005-12-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999130, '12345678-9', '12345678-9', '2005-10-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999131, '12345678-9', '12345678-9', '2005-10-31 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999132, '12345678-9', '12345678-9', '2005-11-30 10:30:00', 1);

insert into bm_bono (dom_tipbon, bo_folio, pb_rut, be_carne, bo_fecemi, ha_codigo) values
('W', 999133, '12345678-9', '12345678-9', '2005-11-31 10:30:00', 1);

/*************************************************************************
	BONO-ITEMS
*************************************************************************/

drop table bm_bonite if exists;

  create table bm_bonite (
  bi_serial integer identity,
  bo_serial integer,
  dp_serial integer,
  dom_tipbon varchar(1),
  pr_codigo integer,
  dom_tippre integer,
  bi_fecate timestamp,
  vc_valor integer,
  bi_apodip integer,
  bi_aposeg integer,
  pa_pabellon integer,
  bi_cantidad integer,
  dom_estliq varchar(100),
  bi_valcon float,
  bi_valbon float,
  cv_codigo integer,
  pr_ctapre varchar(100),
  pr_ctapab varchar(100),
  bi_valpab float,
  bi_copago float,
  bi_rutseg integer,
  bi_fedeho timestamp,
  bi_fehaho timestamp,
  bi_apopab float,
  bi_incpab varchar(1),
  bi_pencom varchar(1),
  bi_paciente varchar(100),
  bi_quien varchar(100),
  dom_ranodi integer
)

/*************************************************************************
	ACREEDOR
*************************************************************************/

drop table acreedor if exists;

create table acreedor (
  acree_rut varchar(20),
  acree_rsocial varchar(100),
  acree_rut_aux integer
);

/*************************************************************************
	BENEFICIARIO
*************************************************************************/

drop table beneficiario if exists;

create table beneficiario (
  rut_bene integer,
  dgv_bene varchar(20),
  nombres varchar(20),
  ape_pat varchar(20),
  ape_mat varchar(20)
);

/*************************************************************************
	CONVENIO
*************************************************************************/

drop table bm_convenio if exists;

create table bm_convenio (
  cv_codigo integer identity,
  pb_codigo integer,
  cv_fecini timestamp,
  cv_fecter timestamp
);

/*************************************************************************
	PRESTADOR DE BENEFICIOS
*************************************************************************/

drop table bm_preben if exists;

create table bm_preben (
  pb_codigo integer,
  pb_rut varchar(20),
  dom_ciudad integer
)

/*************************************************************************
	PRESTADOR (revisar si debe existir)
*************************************************************************/

drop table bm_prestador if exists;

create table bm_prestador (
  cod_prestador varchar(20),
  rut_prestador varchar(20),
  nom_prestador varchar(20),
  dom_ciudad integer
);

/*************************************************************************
	PRESTADOR_CIUDAD (revisar si debe existir)
*************************************************************************/

drop table bm_prestador_ciudad if exists;

create table bm_prestador_ciudad (
  cod_prestador varchar(20),
  dom_ciudad varchar(20)
);

/*************************************************************************
	VALOR CONVENIO
*************************************************************************/

drop table bm_valcon if exists;

create table bm_valcon (
  cv_codigo integer,
  pr_codigo integer
);

/*************************************************************************
	BMW_BONITE (items de bono c/genericas???)
*************************************************************************/

drop table bmw_bonite if exists;
create table bmw_bonite (
  wi_serial integer identity,
  bo_serial integer,
  wi_codigo integer
);

/*************************************************************************
	ROL BENE
*************************************************************************/

drop table rolbene if exists;

create table rolbene (
  cod_repart integer,
  nro_impo integer,
  nro_correl integer,
  rut_bene integer,
  rut_impo integer,
  cod_contrato varchar(20)
);

