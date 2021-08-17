package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

/**
 * 1. 통합 테스트 : 느리다. 테스트를 위해 환경 구축 필요(DB, 외부 API). @SpringBootTest, @Autowired
 * 2. 슬라이싱 테스트 : @ExtendWith(MockitoExtension.class), @Mock, @InjectMocks, given(), verify()
 * 3. 가짜 객체 테스트 : Mockito 를 사용하는 것에 비해 훨씬 빠름 (리플렉션을 사용하지 않기 때문). 아래는 예시
 *
 * 설계가 잘 되어 있다면 가짜 객체 테스트로 충분하지만,
 * End to End 테스트인 경우에는 Mockito 가 쓰일 수 있음.
 *
 * 테스트를 어떻게 작성하느냐는 종교 전쟁과 같은 것.
 */
class ProductServiceTest {
	private ProductRepository productRepository;
	private MenuRepository menuRepository;
	private PurgomalumClient purgomalumClient;
	private ProductService productService;

	@BeforeEach
	void setUp() {
		productRepository = new InMemoryProductRepository();
		menuRepository = new InMemoryMenuRepository();
		purgomalumClient = new FakePurgomalumClient();
		productService = new ProductService(productRepository, menuRepository, purgomalumClient);
	}

	@DisplayName("상품을 등록할 수 있다.")
	@Test
	void create() {
		// given
		final Product expected = createProductRequest("후라이드", 16_000L);

		// when
		final Product actual = productService.create(expected);

		// then
		assertAll(
			() -> assertThat(actual).isNotNull(),
			() -> assertThat(actual.getId()).isNotNull(),
			() -> assertThat(actual.getName()).isEqualTo(expected.getName()),
			() -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice())
		);
	}

	@DisplayName("상품의 가격이 올바르지 않으면 등록할 수 없다.")
	@ValueSource(strings = "-1000")
	@NullSource
	@ParameterizedTest
	void create(final BigDecimal price) {
		// given
		final Product expected = createProductRequest("후라이드", price);

		// when & then
		assertThatThrownBy(() -> productService.create(expected))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("상품의 이름이 올바르지 않으면 등록할 수 없다.")
	@ValueSource(strings = {"비속어", "욕설이 포함된 이름"})
	@ParameterizedTest
	void create(final String name) {
		// given
		final Product expected = new Product();
		expected.setName(name);
		expected.setPrice(BigDecimal.valueOf(16_000L));

		// when & then
		assertThatThrownBy(() -> productService.create(expected))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("상품의 목록을 조회할 수 있다.")
	@Test
	void findAll() {
		// given
		productRepository.save(product("후라이드", 16_000L));
		productRepository.save(product("양념치킨", 16_000L));

		// when
		final List<Product> actual = productService.findAll();

		// then
		assertThat(actual).hasSize(2);
	}

	private Product product(final String name, final long price) {
		final Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName(name);
		product.setPrice(BigDecimal.valueOf(price));
		return product;
	}

	private Product createProductRequest(final String name, final long price) {
		return createProductRequest(name, BigDecimal.valueOf(price));
	}

	private Product createProductRequest(final String name, final BigDecimal price) {
		final Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		return product;
	}
}
