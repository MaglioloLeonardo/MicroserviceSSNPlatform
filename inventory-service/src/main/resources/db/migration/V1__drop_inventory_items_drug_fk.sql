-- Rimuove il vecchio vincolo che puntava alla tabella inesistente "drugs"
ALTER TABLE IF EXISTS inventory_service.inventory_items
    DROP CONSTRAINT IF EXISTS inventory_items_drug_id_fkey;
