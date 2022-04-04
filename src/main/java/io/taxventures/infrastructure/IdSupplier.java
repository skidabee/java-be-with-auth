package io.taxventures.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.util.UUID;

@Service
public class IdSupplier implements IdGenerator {
    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
