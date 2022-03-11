package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  private static final Long NEGATIVE_NUM = -1L;
  private static final BigDecimal PRICE_ZERO = BigDecimal.ZERO;
  public static final UUID RANDOM_UUID = UUID.randomUUID();

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private MenuRepository menuRepository;
  @Mock
  private OrderTableRepository orderTableRepository;
  @Mock
  private KitchenridersClient kitchenridersClient;

  @InjectMocks
  OrderService orderService;

  private static Stream<String> getAddress() {
    return Stream.of(
            "",
            null
    );
  }

  @ParameterizedTest
  @EnumSource(value = OrderType.class, names = {"DELIVERY"})
  @DisplayName("배달인 경우 주문 시작]")
  void createOrderDelivery(OrderType orderType) {
    //given
    Order request = order();
    Menu menu = menu();
    request.setType(orderType);

    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    assertDoesNotThrow(() -> {
      orderService.create(request);
    });
  }

  @ParameterizedTest
  @EnumSource(value = OrderType.class, names = {"TAKEOUT", "DELIVERY"})
  @DisplayName("주문 타입이 배달이거나 포장일 경우, 각각 상품품목 수량이 음수이면, IllegalArgumentException 예외 발생")
  void checkQuantity(OrderType orderType) {
    //given
    Order request = order();
    Menu menu = menu();

    request.setType(orderType);

    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    request.getOrderLineItems().forEach(orderLineItem -> {
      orderLineItem.setQuantity(NEGATIVE_NUM);
    });

    //then
    assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("배달인 경우 주문 시작 : 배달 주문일 때, 주소가 없으면 IllegalArgumentException 예외 발생")
  void DeliveryNeedAddress(String address) {
    //given
    Order request = order();
    Menu menu = menu();

    request.setType(OrderType.DELIVERY);
    //when
    request.setDeliveryAddress(address);
    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @EnumSource(value = OrderType.class, names = {"EAT_IN"})
  @DisplayName("손님은 가게에 주문을 할 수 있습니다.")
  void createOrderEatIn(OrderType orderType) {
    //given
    Order request = order();
    Menu menu = menu();

    request.setType(orderType);
    request.getOrderTable().setEmpty(true);

    when(orderTableRepository.findById(any())).thenReturn(Optional.of(request.getOrderTable()));
    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    assertDoesNotThrow(() -> {
      orderService.create(request);
    });
  }

  @ParameterizedTest
  @EnumSource(value = OrderType.class, names = {"EAT_IN"})
  @DisplayName("매장 내 식사 주문인 경우 주문 테이블이 비어있지 않으면 IllegalStateException 예외 발생")
  void EatInNeedOrderTable(OrderType orderType) {
    //given
    Order request = order();
    Menu menu = menu();

    //when
    request.setType(orderType);
    request.getOrderTable().setEmpty(false);
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(request.getOrderTable()));
    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
    //then
    assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @EnumSource(value = OrderType.class)
  @DisplayName("주문할 때, 메뉴가 숨김 처리가 되어 있으면, IllegalStateException 예외 발생")
  void menuDisabledFalse(OrderType orderType) {
    //given
    Order request = order();
    Menu menu = menu();

    //when
    menu.setDisplayed(false);
    request.setType(orderType);
    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @EnumSource(value = OrderType.class)
  @DisplayName("메뉴 가격과 주문하려는 메뉴의 상품품목의 가격이 같지 않으면 IllegalArgumentException 예외 발생")
  void checkMenuPrice(OrderType orderType) {
    //given
    Order request = order();
    Menu menu = menu();

    request.setType(orderType);
    //when
    menu.setPrice(PRICE_ZERO);

    when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @DisplayName("배달을 제외한 주문 요청을 허락 상태로 변경")
  @EnumSource(value = OrderType.class, names = {"DELIVERY"}, mode = EnumSource.Mode.EXCLUDE)
  void acceptOrderExcludeDelivery(OrderType orderType) {
    //given
    Order order = order();

    //when
    order.setStatus(OrderStatus.WAITING);
    order.setType(orderType);

    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertDoesNotThrow(() -> {
      orderService.accept(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("배달 주문일 때, 주문 요청을 수락으로 변경.")
  @EnumSource(value = OrderType.class, names = {"DELIVERY"})
  void acceptOrderDeliveryType(OrderType orderType) {
    //given
    Order order = order();

    //when
    order.setType(orderType);
    order.setStatus(OrderStatus.WAITING);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertDoesNotThrow(() -> {
      orderService.accept(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("주문 수락을 누를 때, 주문의 상태가 수락전이 아닌 경우, IllegalStateException 예외 발생")
  @EnumSource(value = OrderStatus.class, names = {"WAITING"}, mode = EnumSource.Mode.EXCLUDE)
  void acceptOrderOnlyWaitingType(OrderStatus orderStatus) {
    //given
    Order order = order();

    //when
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertThatThrownBy(() -> orderService.accept(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("가게 점주는 주문 상태를 준비 상태로 변경합니다.")
  @EnumSource(value = OrderStatus.class, names = {"ACCEPTED"})
  void serve(OrderStatus orderStatus) {
    //given
    Order order = order();
    //when
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    //then
    assertDoesNotThrow(()->{
      orderService.serve(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("주문 준비를 하기 전 기존 주문의 상태는 수락이 아닌 경우, IllegalStateException 예외 발생 ")
  @EnumSource(value = OrderStatus.class, names = {"ACCEPTED"}, mode = EnumSource.Mode.EXCLUDE)
  void serveOnlyAccepted(OrderStatus orderStatus) {
    //given
    Order order = order();
    //when
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    //then
    assertThatThrownBy(() -> orderService.serve(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("주문 상태를 배송중 상태로 변경합니다.")
  @EnumSource(value = OrderType.class, names = {"DELIVERY"})
  void startDelivery(OrderType orderType) {
    //given
    Order order = order();
    //when
    order.setType(orderType);
    order.setStatus(OrderStatus.SERVED);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    assertDoesNotThrow(()->{
      orderService.startDelivery(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("배송 중 상태로 변경하기전 기존 주문 방식이 배달이 아니면, IllegalStateException 예외 발생")
  @EnumSource(value = OrderType.class, names = {"DELIVERY"}, mode = EnumSource.Mode.EXCLUDE)
  void startDeliveryOnlyDelivery(OrderType orderType) {
    //given
    Order order = order();
    //when
    order.setType(orderType);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> orderService.startDelivery(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("배송을 시작할 떄, 기존 주문 상태는 준비중이 아니면 IllegalStateException 예외 발생")
  @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
  void startDeliveryOnlyServed(OrderStatus orderStatus) {
    //given
    Order order = order();
    //when
    order.setType(OrderType.DELIVERY);
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> orderService.startDelivery(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("배달 타입의 주문 상태를 완료 상태로 변경합니다.")
  @EnumSource(value = OrderStatus.class, names = {"DELIVERING"})
  void completeDelivery(OrderStatus orderStatus) {
    //given
    Order order = order();
    //when
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    assertDoesNotThrow(()->{
      orderService.completeDelivery(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("완료 상태로 변경할 때, 기존 주문 상태는 배송중이 아니면, IllegalStateException 예외 발생")
  @EnumSource(value = OrderStatus.class, names = {"DELIVERING"}, mode = EnumSource.Mode.EXCLUDE)
  void completeDeliveryOnlyDelivering(OrderStatus orderStatus) {
    //given
    Order order = order();
    //when
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> orderService.completeDelivery(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("배달 타입의 주문 상태를 완료 상태로 변경합니다.")
  @EnumSource(value = OrderType.class, names = {"DELIVERY"})
  void completeDelivery(OrderType orderType) {
    //given
    Order order = order();

    //when
    order.setType(orderType);
    order.setStatus(OrderStatus.DELIVERED);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertDoesNotThrow(()->{
      orderService.complete(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("배달 타입의 주문 상태를 완료 상태로 변경할 때, 상태가 배송 중일 때가 아니면 IllegalStateException 예외 발생 ")
  @EnumSource(value = OrderStatus.class, names = {"DELIVERED"}, mode = EnumSource.Mode.EXCLUDE)
  void completeDeliveryOnlyDelivered(OrderStatus orderStatus) {
    //given
    Order order = order();

    //when
    order.setType(OrderType.DELIVERY);
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertThatThrownBy(() -> orderService.complete(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("포장 타입의 주문 상태를 완료 상태로 변경합니다")
  @EnumSource(value = OrderStatus.class, names = {"SERVED"})
  void completeTakeOutAndEanIn(OrderStatus orderStatus) {
    //given
    Order order = order();

    //when
    order.setType(OrderType.TAKEOUT);
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertDoesNotThrow(()->{
      orderService.complete(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("포장 타입의 주문 상태를 완료 상태로 변경할 때, 상태가 준비중이 아니면, IllegalStateException 예외 발생 ")
  @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
  void completeTakeOutOnlyServed(OrderStatus orderStatus) {
    //given
    Order order = order();

    //when
    order.setType(OrderType.TAKEOUT);
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertThatThrownBy(() -> orderService.complete(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @DisplayName("매장 식사 타입의 주문 상태를 완료 상태로 변경합니다")
  @EnumSource(value = OrderStatus.class, names = {"SERVED"})
  void completeEanIn(OrderStatus orderStatus) {
    //given
    Order order = order();

    //when
    order.setType(OrderType.EAT_IN);
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

    //then
    assertDoesNotThrow(()->{
      orderService.complete(RANDOM_UUID);
    });
  }

  @ParameterizedTest
  @DisplayName("매장 식사 타입의 주문 상태를 완료 상태로 변경할 때, 상태가 준비중이어야 합니다. ")
  @EnumSource(value = OrderStatus.class, names = {"SERVED"}, mode = EnumSource.Mode.EXCLUDE)
  void completeEatInOnlyServed(OrderStatus orderStatus) {
    //given
    Order order = order();

    //when
    order.setType(OrderType.EAT_IN);
    order.setStatus(orderStatus);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    //then
    assertThatThrownBy(() -> orderService.complete(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("가게 점주는 주문 정보를 모두 조회할 수 있습니다.")
  void findAll() {
    //given
    when(orderRepository.findAll()).thenReturn(Collections.singletonList(order()));

    //then
    orderService.findAll();
    verify(orderRepository).findAll();
  }
}