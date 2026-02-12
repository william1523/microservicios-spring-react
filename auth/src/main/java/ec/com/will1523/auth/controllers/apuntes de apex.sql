apuntes de apex

Select
SEQ_ID, 
N001 as sproducto,
C001 as codigo,
C002 as descripcion,
N002 as cantidad,
N003 as preciounitario,
N004 as descuento,
N005 as sfacturadetalle
FROM APEX_collections
WHERE collection_name = 'DETALLE_FACTURA'
and seq_id=:P5_SEQ_ID

consulta para cargar la collection_name

p_N001=> item.seq_id,
                    P_C001=> item.CODIGO,
                    P_C002=> item.DESCRIPCION

 declare v_sfacturadetalle number;
 begin
    --calcular totales
    --APEX_DEBUG_MESSAGE.ENABLE_DEBUG_MESSAGES(p_level => 3);
    Select
            round(sum(nvl(N002,0) * NVL(N003,0)),2) as subtotal, 
            round(sum(nvl(N004,0)),2) as descuento,
            round(sum(nvl(N002,0) * NVL(N003,0)) - sum(nvl(N004,0)),2)  as total
            into :P4_SUBTOTAL,:P4_DESCUENTO,:P4_TOTAL
    FROM APEX_collections
    WHERE collection_name = 'DETALLE_FACTURA';
    if :P4_SFACTURA is null then
        select nvl(max(sfactura),0)+1 into :P4_SFACTURA from wksp_comprobantes.com_facturas
        where cempresa=:var_id_empresa;
        --secuencial factura
        insert into wksp_comprobantes.com_facturas (CEMPRESA, SFACTURA, CESTABLECIMIENTO, CPUNTOEMISION, CAMBIENTE, USRCREACION, FCREACION, SCLIENTE, CFORMAPAGO, SUBTOTAL, DESCUENTO, TOTAL, ESTADO ) values 
        (:var_id_empresa, :P4_SFACTURA,:P4_CESTABLECIMIENTO, :P4_CPUNTOEMISION,:P4_CAMBIENTE, :P4_USRCREACION, :P4_FCREACION, :P4_SCLIENTE, :P4_CFORMAPAGO, :P4_SUBTOTAL, :P4_DESCUENTO, :P4_TOTAL, :P4_ESTADO  );
        for item in (Select
            SEQ_ID as sfacturadetalle, 
            N001 as sproducto,
            C001 as codigo,
            C002 as descripcion,
            N002 as cantidad,
            N003 as preciounitario,
            N004 as descuento,
            round((N002 * N003),2) as subtotal,
            round(((N002 * N003) - N004),2) as total
            FROM APEX_collections
            WHERE collection_name = 'DETALLE_FACTURA')  loop

                     insert into COM_DETALLESFACTURA (CEMPRESA, SFACTURA, SFACTURADETALLE, SPRODUCTO, CANTIDAD, PRECIOUNITARIO, SUBTOTAL, DESCUENTO, TOTAL)
                         values(:VAR_ID_EMPRESA,:P4_SFACTURA,item.sfacturadetalle, item.sproducto, item.cantidad, item.preciounitario, item.subtotal, item.descuento, item.total);
        end loop;
        for itemIA in (Select
            SEQ_ID as sdetalleadicionalfactura, 
            C001 as codigo,
            C002 as descripcion
            FROM APEX_collections
            WHERE collection_name = 'DETALLE_ADICIONAL_FACTURA')  loop
                     insert into COM_DETALLESADICIONALESFACTURA (CEMPRESA, SFACTURA, SDETALLEADICIONALFACTURA, NOMBRE, VALOR)
                         values(:VAR_ID_EMPRESA,:P4_SFACTURA,itemIA.sdetalleadicionalfactura, itemIA.codigo,itemIA.descripcion);
        end loop;
        commit;
        APEX_COLLECTION.TRUNCATE_COLLECTION (p_collection_name => 'DETALLE_FACTURA');
        APEX_COLLECTION.TRUNCATE_COLLECTION (p_collection_name => 'DETALLE_ADICIONAL_FACTURA');
        
    else 
        update wksp_comprobantes.com_facturas 
        set CESTABLECIMIENTO=:P4_CESTABLECIMIENTO,CPUNTOEMISION=:P4_CPUNTOEMISION, FCREACION=:P4_FCREACION, SCLIENTE=:P4_SCLIENTE, CFORMAPAGO=:P4_CFORMAPAGO, SUBTOTAL=:P4_SUBTOTAL, DESCUENTO=:P4_DESCUENTO, TOTAL=:P4_TOTAL 
        where CEMPRESA=:var_id_empresa and  SFACTURA=:P4_SFACTURA;
        --elimnar los borrados
        for borrado in (select sfacturadetalle from COM_DETALLESFACTURA where cempresa=:var_id_empresa and sfactura=:P4_SFACTURA and sfacturadetalle in (
                select sfacturadetalle from COM_DETALLESFACTURA where cempresa=:var_id_empresa and sfactura=:P4_SFACTURA
                minus
                select N005 as sfacturadetalle FROM APEX_collections WHERE collection_name = 'DETALLE_FACTURA'
        ) )loop
            APEX_DEBUG_MESSAGE.LOG_MESSAGE(
                        p_message => 'Borrar detalle de factura: '||borrado.sfacturadetalle,
                        p_level => 1 
                        ); 
            delete from COM_DETALLESFACTURA where cempresa=:var_id_empresa and sfactura=:P4_SFACTURA  and sfacturadetalle=borrado.sfacturadetalle;
        end loop;
        --elimnar los borrados en detalles adicionales
        for borrado in (select sdetalleadicionalfactura from COM_DETALLESADICIONALESFACTURA where cempresa=:var_id_empresa and sfactura=:P4_SFACTURA and sdetalleadicionalfactura in (
                select sdetalleadicionalfactura from COM_DETALLESADICIONALESFACTURA where cempresa=:var_id_empresa and sfactura=:P4_SFACTURA
                minus
                select N001 as sdetalleadicionalfactura FROM APEX_collections WHERE collection_name = 'DETALLE_ADICIONAL_FACTURA'
        ) )loop
            APEX_DEBUG_MESSAGE.LOG_MESSAGE(
                        p_message => 'Borrar detalle de info adicional factura: '||borrado.sdetalleadicionalfactura,
                        p_level => 1 
                        ); 
            delete from COM_DETALLESADICIONALESFACTURA where cempresa=:var_id_empresa and sfactura=:P4_SFACTURA  and sdetalleadicionalfactura=borrado.sdetalleadicionalfactura;
        end loop;


        --Detalles de la colección.
        --valor de codigo para items nuevos 
        select nvl(max(sfacturadetalle),0)+1 into v_sfacturadetalle 
        from wksp_comprobantes.COM_DETALLESFACTURA
        where cempresa=:var_id_empresa
        and sfactura=:P4_SFACTURA;
        if v_sfacturadetalle is null then
            v_sfacturadetalle:=1;
        end if;
        APEX_DEBUG_MESSAGE.LOG_MESSAGE(
        p_message => 'VALOR PARA LA SECUENCIA DE ITEMS NUEVOS cempresa, sfactura :'|| :var_id_empresa||',' ||:P4_SFACTURA ||' : ' ||v_sfacturadetalle,
        p_level => 1 );
        for item in (Select 
            N001 as sproducto,
            C001 as codigo,
            C002 as descripcion,
            N002 as cantidad,
            N003 as preciounitario,
            N004 as descuento,
            N005 as sfacturadetalle,
            round((N002 * N003),2) as subtotal,
            round(((N002 * N003) - N004),2) as total
            FROM APEX_collections
            WHERE collection_name = 'DETALLE_FACTURA')  loop
              
                if item.sfacturadetalle is null then
                    insert into COM_DETALLESFACTURA (CEMPRESA, SFACTURA, SFACTURADETALLE, SPRODUCTO, CANTIDAD, PRECIOUNITARIO, SUBTOTAL, DESCUENTO, TOTAL)
                         values(:VAR_ID_EMPRESA,:P4_SFACTURA,v_sfacturadetalle, item.sproducto, item.cantidad, item.preciounitario, item.subtotal, item.descuento, item.total);
          
                    v_sfacturadetalle:=v_sfacturadetalle+1;  
                    APEX_DEBUG_MESSAGE.LOG_MESSAGE(
                        p_message => 'VALOR PARA LA SECUENCIA DE ITEMS NUEVOS '||v_sfacturadetalle,
                        p_level => 1 
                        );   
                else
                    update COM_DETALLESFACTURA set SPRODUCTO=item.sproducto, CANTIDAD=item.cantidad, PRECIOUNITARIO=item.preciounitario, SUBTOTAL=item.subtotal, DESCUENTO=item.descuento, TOTAL=item.total
                    where cempresa=:var_id_empresa
                    and sfactura=:P4_SFACTURA
                    and sfacturadetalle=item.sfacturadetalle;
                end if;
            end loop;

            

            commit;
            APEX_COLLECTION.TRUNCATE_COLLECTION (
                p_collection_name => 'DETALLE_FACTURA');
            APEX_COLLECTION.TRUNCATE_COLLECTION (
                p_collection_name => 'DETALLE_ADICIONAL_FACTURA');
                
    end if;

    
end;