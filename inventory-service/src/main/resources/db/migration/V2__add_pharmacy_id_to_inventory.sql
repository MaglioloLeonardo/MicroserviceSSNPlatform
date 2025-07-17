BEGIN;

-- 1) Aggiungi prima la colonna nullable
ALTER TABLE inventory_service.inventory_items
    ADD COLUMN pharmacy_id UUID;

-- 2) Popola il campo per le righe esistenti
UPDATE inventory_service.inventory_items
SET pharmacy_id = '00000000-0000-0000-0000-000000000000'
WHERE pharmacy_id IS NULL;

-- 3) Imposta la colonna NOT NULL
ALTER TABLE inventory_service.inventory_items
    ALTER COLUMN pharmacy_id SET NOT NULL;

COMMIT;
