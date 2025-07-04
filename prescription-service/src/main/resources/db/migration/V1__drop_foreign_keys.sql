-- Rimuove (se esiste) il vincolo che blocca l'inserimento
ALTER TABLE IF EXISTS prescription_service.prescriptions
    DROP CONSTRAINT IF EXISTS prescriptions_doctor_id_fkey;
