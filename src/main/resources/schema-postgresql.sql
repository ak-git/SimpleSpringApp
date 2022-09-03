CREATE OR REPLACE RULE person_del_protect AS ON DELETE TO person DO INSTEAD NOTHING;
CREATE OR REPLACE RULE person_update_protect AS ON UPDATE TO person DO INSTEAD NOTHING;

CREATE OR REPLACE RULE player_del_protect AS ON DELETE TO player DO INSTEAD NOTHING;
CREATE OR REPLACE RULE player_update_protect AS ON UPDATE TO player DO INSTEAD NOTHING;

CREATE OR REPLACE VIEW person_view AS
    select a.* from person a left outer join person b ON a.uuid = b.uuid AND a.revision < b.revision
               where b.uuid is null and NULLIF(a.password, '') IS NOT NULL
               order by a.revision desc nulls last;

CREATE OR REPLACE VIEW player_view AS
    select a.* from player a left outer join player b ON a.uuid = b.uuid AND a.revision < b.revision where b.uuid is null
      and (NULLIF(a.first_name, '') IS NOT NULL or NULLIF(a.sur_name, '') IS NOT NULL or NULLIF(a.last_name, '') IS NOT NULL)
      order by a.revision desc nulls last;