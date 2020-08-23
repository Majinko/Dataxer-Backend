package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.dto.PaymentDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentMapper {

    Payment paymentDTOtoPayment(PaymentDTO paymentDTO);

    PaymentDTO paymentToPaymentDTOSimple(Payment payment);

    PaymentDTO paymentToPaymentDTO(Payment payment);

}
