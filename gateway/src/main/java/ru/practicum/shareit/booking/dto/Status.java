package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum Status {
	// Все
	ALL,
	// Текущие
	CURRENT,

	APPROVED,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static Optional<Status> from(String stringState) {
		for (Status status: values()) {
			if (status.name().equalsIgnoreCase(stringState)) {
				return Optional.of(status);
			}
		}
		return Optional.empty();
	}
}
