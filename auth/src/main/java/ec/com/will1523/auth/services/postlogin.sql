VAR_CAMBIENTE	-	Restricted - May not be set from browser	Application	-	-	-	-	-	
Copy
VAR_CESTABLECIMIENTO	-	Restricted - May not be set from browser	Application	-	-	-	-	-	
Copy
VAR_CPUNTOEMISION	-	Restricted - May not be set from browser	Application	-	-	-	-	-	
Copy
VAR_ID_EMPRESA	-	Restricted - May not be set from browser	Global	-	-	-	-	-	
Copy
VAR_NOMBRE_EMPRESA	-	Restricted - May not be set from browser	Application	-	-	-	-	-	
Copy
VAR_SIMPUESTOICE	-	Restricted - May not be set from browser	Application	-	-	-	1.2 years ago	COMPROBANTES	
Copy
VAR_SIMPUESTOIVA

apex_authentication.login(p_username => :P9999_USERNAME, p_password => :P9999_PASSWORD);
select e.cempresa, nvl(e.nombrecomercial,e.razonsocial),ue.cambiente,ue.cestablecimiento,ue.cpuntoemision
into :VAR_ID_EMPRESA, :VAR_NOMBRE_EMPRESA , :VAR_CAMBIENTE, :VAR_CESTABLECIMIENTO,:VAR_CPUNTOEMISION
from com_usuariosempresa ue,
com_empresas e
where upper(ue.usuario) = upper(:P9999_USERNAME)
and e.cempresa=ue.cempresa;
select SIMPUESTO into :VAR_SIMPUESTOIVA from WKSP_COMPROBANTES.COM_IMPUESTOS where impuesto='IVA';
select SIMPUESTO into :VAR_SIMPUESTOICE from WKSP_COMPROBANTES.COM_IMPUESTOS where impuesto='ICE';

BEGIN
  APEX_UTIL.SET_SESSION_STATE(
    p_name  => 'YOUR_APPLICATION_ITEM_NAME', -- Replace with the actual application item name
    p_value => 'Value to be logged'           -- Replace with the value you want to log
  );
END;

-- Ejemplo de lógica que ejecutaría tu proceso de gestión de usuarios:
BEGIN
    -- 1. Crear el usuario en el workspace de APEX
    APEX_UTIL.CREATE_USER (
        p_user_name       => :P1_USERNAME,
        p_email_address   => :P1_EMAIL,
        p_web_password    => :P1_PASSWORD,
        p_developer_privs => 'N',
        p_change_password_on_first_use => 'Y'
    );
    
    -- 2. Insertar el perfil en tu tabla VPD local
    INSERT INTO USUARIOS (ID_USUARIO, NOMBRE_USUARIO, EMPRESA_ID, ROL)
    VALUES (SEQ_USUARIOS.NEXTVAL, :P1_USERNAME, :P1_EMPRESA_ASIGNADA, :P1_ROL);
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- Manejar errores y hacer ROLLBACK para evitar datos inconsistentes
        ROLLBACK;
        RAISE;
END;

CREATE OR REPLACE PACKAGE BODY PKG_SEGURIDAD_KARDEX AS
    -- ...
    PROCEDURE SET_EMPRESA_CONTEXT (
        p_username IN VARCHAR2
    )
    IS
        v_empresa_id USUARIOS.EMPRESA_ID%TYPE;
        v_user_id USUARIOS.ID_USUARIO%TYPE;
    BEGIN
        -- 1. Intentar buscar el perfil de empresa del usuario
        BEGIN
            SELECT EMPRESA_ID, ID_USUARIO
            INTO v_empresa_id, v_user_id
            FROM USUARIOS
            WHERE NOMBRE_USUARIO = p_username;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                -- 2. Si no se encuentra el perfil, ASIGNARLO a una EMPRESA POR DEFECTO (p. ej., ID 1)
                -- Esto asegura que, si fue creado solo en APEX, al menos tendrá acceso a datos limitados.
                INSERT INTO USUARIOS (ID_USUARIO, NOMBRE_USUARIO, EMPRESA_ID, ROL)
                VALUES (SEQ_USUARIOS.NEXTVAL, p_username, 1, 'BASICO') -- Usar una EMPRESA_ID y ROL por defecto
                RETURNING ID_USUARIO INTO v_user_id; 

                v_empresa_id := 1;
        END;
        
        -- 3. Establecer el Contexto de la Aplicación
        DBMS_SESSION.SET_CONTEXT (
            NAMESPACE => CONTEXT_NAME,
            ATTRIBUTE => 'EMPRESA_ID',
            VALUE     => v_empresa_id
        );
        -- ...
    END SET_EMPRESA_CONTEXT;
    -- ...
END PKG_SEGURIDAD_KARDEX;
/