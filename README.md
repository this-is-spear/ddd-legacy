# 키친포스

## 요구 사항
치킨 판매 주문 서비스 구현

- [ ] 메뉴(menu)
  - [ ] 도메인 정보
    - [ ] 메뉴는 이름과 가격을 가집니다.
    - [ ] 메뉴는 메뉴 그룹에 포함이 됩니다.
    - [ ] 메뉴 숨김 처리 정보를 포함합니다.
    - [ ] 메뉴는 메뉴에 올라간 상품 정보를 포함합니다.
  - [ ] 서비스
    - [ ] 사용자는 메뉴를 추가 할 수 있습니다.
      - [ ] 메뉴의 가격은 0보다 높습니다.
      - [ ] 메뉴에 올라간 상품 정보는 0보다 큽니다.
      - [ ] 메뉴에 올리려는 상품의 정보와 실제 상품 정보의 개수는 같습니다.
      - [ ] 메뉴에 올리려는 상품의 정보의 수량은 0보다 큽니다.
      - [ ] 상품의 정보를 가져와 메뉴에 올리고, 메뉴에 올라간 상품의 수량을 변경합니다.
    - [ ] 사용자는 가격을 변경할 수 있습니다.
      - [ ] 변경하려는 메뉴의 가격을 가져옵니다.
      - [ ] 변경하려는 메뉴의 가격은 0보다 높습니다.
      - [ ] 메뉴 가격은 메뉴의 상품들 가격의 합보다 비쌀 수 없습니다.
    - [ ] 사용자는 메뉴가 숨김 처리를 해제할 수 있습니다.
      - [ ] 메뉴에 올라간 상품의 가격은 0보다 큽니다.
      - [ ] 메뉴 가격은 메뉴의 상품들 가격의 합보다 비쌀 수 없습니다.
    - [ ] 사용자는 메뉴를 숨김 처리할 수 있습니다.
      - [ ] 메뉴가 표시되지 않도록 설정합니다.
    - [ ] 사용자는 메뉴를 전부 조회할 수 있습니다.
- [ ] 메뉴 그룹(menu_group)
  - [ ] 도메인 정보
    - [ ] 메뉴를 분류하는 이름을 가집니다.
  - [ ] 서비스
    - [ ] 사용자는 메뉴 그룹을 생성할 수 있습니다.
      - [ ] 메뉴를 분류할 수 있도록 이름을 지정해 생성합니다.
    - [ ] 사용자는 모든 메뉴 그룹을 가져올 수 있습니다.
- [ ] 주문(order)
  - [ ] 도메인 정보
    - [ ] 주문은 주문의 타입과 스테이터스, 주문 날짜, 배송지, 주문품목 리스트를 가집니다.
    - [ ] 주문은 주문 테이블의 정보를 가지고 있습니다.
    - [ ] 주문 상태에는 수락전, 수락, 준비중, 배송중, 배송완료, 완료 중, 하나의 주문 상태를 포함합니다.
    - [ ] 주문 방식에는 배달, 포장, 매장식사 중, 하나의 주문 타입 정보를 포합합니다.
  - [ ] 서비스
    - [ ] 사용자는 주문을 생성할 수 있습니다.
      - [ ] 주문 안에 주문품목은 0보다 큽니다.
      - [ ] 주문 상태가 매장 내 식사가 아닌 경우, 상품품목의 수량이 0보다 작으면 안됩니다.
      - [ ] 메뉴가 숨김 처리가 되어 있으면 주문할 수 없습니다.
      - [ ] 메뉴의 가격과 주문하려는 메뉴의 상품품목의 가격은 같아야 합니다.
      - [ ] 배달 방식의 주문인 경우 배달주소가 필요합니다.
      - [ ] 매장식사 방식의 주문인 경우 주문 테이블을 이용할 수 없는 상태일 때, 주문이 불가능합니다.
      - [ ] 생성된 주문은 대기 상태를 가집니다.
    - [ ] 사용자는 주문 요청을 허락할 수 있습니다.
      - [ ] 주문의 상태가 수락전 주문만 수락이 가능합니다.
      - [ ] 사용자의 주문 방식이 배달이면 배달을 요청합니다.
    - [ ] 사용자는 주문의 상태를 변경할 수 있습니다.
      - [ ] 사용자는 주문 상태를 준비 상태로 변경합니다.
        - [ ] 기존 주문의 상태는 수락이어야 합니다.
      - [ ] 주문 상태를 배송중 상태로 변경합니다.
        - [ ] 기존 주문 방식은 배달이어야 합니다.
        - [ ] 기존 주문 상태는 준비중이어야 합니다.
      - [ ] 주문 상태를 완료 상태로 변경합니다.
        - [ ] 기존 주문 상태는 배송중이어야 합니다.  
      - [ ] 주문 상태를 완료 상태로 변경합니다.
        - [ ] 기존 주문 방식이 배달인 경우, 상태가 배송중이어야 합니다.
        - [ ] 기존 주문 방식이 포장인 경우, 상태가 준비중이어야 합니다.
        - [ ] 기존 주문 방식이 매장식사인 경우, 상태가 준비중이어야 합니다.
    - [ ] 사용자는 주문 정보를 모두 조회할 수 있습니다.
- [ ] 주문품목(order_line_item)
  - [ ] 도메인 정보
    - [ ] 주문품목에는 메뉴의 정보를 포함합니다.
    - [ ] 주문품목에는 수량과 가격을 포함합니다.
- [ ] 주문 테이블(order_table)
  - [ ] 도메인 정보
    - [ ] 주문 테이블에는 손님의 수 정보를 포합 합니다.
    - [ ] 주문 테이블에는 이름과 비었는지 확인할 수 있는 여부의 정보를 포합합니다.
  - [ ] 서비스
    - [ ] 사용자는 주문 테이블을 이용할 수 있습니다.
    - [ ] 사용자는 주문을 받고 주문 테이블의 상태를 비어있지 않도록 변경할 수 있습니다.
    - [ ] 사용자는 주문이 완료되고 나면 주문 테이블의 상태를 비어있도록 변경할 수 있습니다.
      - [ ] 주문테이블의 주문 상태가 완료인 경우에만 비어있도록 상태를 변경할 수 있습니다.
      - [ ] 주문테이블이 비어있을 시, 해당 테이블의 고객 수는 0으로 변경입니다.
    - [ ] 사용자는 주문 변동에 따라 손님의 수를 변경할 수 있습니다.
      - [ ] 손님의 수는 0보다 커야합니다.
    - [ ] 사용자는 주문 테이블의 모든 정보를 가져올 수 있습니다.
- [ ] 상품(product)
  - [ ] 도메인 정보
    - [ ] 상품에는 이름과 가격 정보를 포함합니다.
  - [ ] 서비스
    - [ ] 상품을 생성합니다.
      - [ ] 상품의 가격은 0보다 큽니다.
    - [ ] 상품의 가격을 변경합니다.
      - [ ] 상품의 가격은 0보다 큽니다.
      - [ ] 메뉴의 가격이 메뉴에 올라간 상품의 가격의 합보다 크면 메뉴가 숨김 처리가 됩니다.
    - [ ] 상품의 모든 정보를 가져옵니다.
- [ ] 메뉴에 올라간 상품(menu_product)
  - [ ] 도메인 정보
    - [ ] 메뉴에 올라간 상품은 수량을 포함합니다.
    - [ ] 메뉴에 올라간 상품은 해당 상품의 정보를 포함합니다.

## 용어 사전

| 한글명           | 영문명             | 설명                         |
|---------------|-----------------|----------------------------|
| menu          | 메뉴              | 메뉴의 정보                     |
| 숨김 처리         | Displayed       | 메뉴를 표시할지 않할지 여부            |
| 메뉴 분류         | MenuGroup       | 메뉴 분류 정보                   |
| 주문 상태         | Status          | 고객의 주문 상태                  |
| 수락전           | WAITING         | 고객의 주문을 수락하기 전 상태          |
| 수락            | ACCEPTED        | 고객의 주문을 수락한 상태             |
| 준비중           | SERVED          | 고객의 주문을 준비중인 상태            |
| 배송중           | DELIVERING      | 고객에게 음식을 배송 중인 상태          |
| 배송완료          | DELIVERED       | 고객의 집으로 음식이 도착한 상태         |
| 완료            | COMPLETED       | 고객에게 음식을 제공한 상태            |
| 주문 방식         | Type            | 고객의 주문 방식                  |
| 배달            | DELIVERY        | 고객이 음식을 배달로 받기 원하는 상태      |
| 포장            | TAKEOUT         | 고객이 음식을 포장으로 받기 원하는 상태     |
| 매장            | EAT_IN          | 고객이 음식을 매장 내에서 식사하기 원하는 상태 |
| 메뉴에 올라가 있는 상품 | MenuProduct     | 메뉴에 올라간 상품 정보              |
| 배달주소          | DeliveryAddress | 고객이 배달로 수령하려는 주소           |

## 모델링
