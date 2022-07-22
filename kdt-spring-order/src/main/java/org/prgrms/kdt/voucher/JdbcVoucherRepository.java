package org.prgrms.kdt.voucher;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcVoucherRepository implements VoucherRepository{
    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.empty();
    }

    @Override
    public Voucher insert(Voucher voucher) {
        return null;
    }
}
