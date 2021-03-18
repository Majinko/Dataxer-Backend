package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.dto.PaymentDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface PaymentMapper {
    Payment paymentDTOtoPayment(PaymentDTO paymentDTO);

    @Named(value = "paymentToPaymentDTOSimple")
    PaymentDTO paymentToPaymentDTOSimple(Payment payment);

    PaymentDTO paymentToPaymentDTO(Payment payment);

    @IterableMapping(qualifiedByName = "paymentToPaymentDTOSimple")
    List<PaymentDTO> paymentsToPaymentDTOs(List<Payment> payments);
}
