package com.personalfinance.dto;

import java.util.List;

public record TransactionsResponse(List<TransactionResponse> transactions) {
}
