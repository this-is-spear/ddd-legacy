package calculation.number;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Numbers {
	public static final Numbers EMPTY = new Numbers(new ArrayList<>());
	private final List<Number> numbers;

	public Numbers(List<Number> numbers) {
		this.numbers = numbers;
	}

	public int sum() {
		return numbers.stream().reduce(Number::sum).orElse(Number.EMPTY).getValue();
	}
}
