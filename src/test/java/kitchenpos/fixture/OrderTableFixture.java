package kitchenpos.fixture;

import java.util.UUID;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
	public static OrderTable orderTable() {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(UUID.randomUUID());
		orderTable.setName("9번");
		orderTable.setNumberOfGuests(0);
		orderTable.setEmpty(true);
		return orderTable;
	}

	public static OrderTable sat() {
		OrderTable orderTable = new OrderTable();
		orderTable.setId(UUID.randomUUID());
		orderTable.setName("9번");
		orderTable.setNumberOfGuests(0);
		orderTable.setEmpty(false);
		return orderTable;
	}
}
