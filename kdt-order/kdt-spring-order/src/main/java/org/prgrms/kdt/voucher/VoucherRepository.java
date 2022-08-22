package org.prgrms.kdt.voucher;

import org.prgrms.kdt.voucher.Voucher;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository {
    Optional<Voucher> findById(UUID voucherId);
    Voucher insert(Voucher voucher);
}
