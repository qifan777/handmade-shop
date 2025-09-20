export default defineAppConfig({
  pages: [
    "pages/index/index",
    "pages/product/product-category-list",
    "pages/product/product-details",
    "pages/cart/cart-list",
    "pages/order/order-create",
    "pages/order/order-list",
    "pages/order/order-details",
    "pages/user/index",
    "pages/user/user-edit",
    "pages/feedback/index",
    "pages/handicraft/handicraft-list",
    "pages/handicraft/handicraft-details",
    "pages/handicraft/handicraft-order-create",
    "pages/handicraft/handicraft-order-list",
    "pages/vip/index",
    "pages/vip/vip-level-list",
    "pages/address/address-list",
    "pages/address/address-save",
  ],
  window: {
    backgroundTextStyle: "light",
    navigationBarBackgroundColor: "#fff",
    navigationBarTextStyle: "black",
  },
  tabBar: {
    borderStyle: "black",
    backgroundColor: "#ffffff",
    color: "#8F8F94",
    selectedColor: "#4C7459",
    list: [
      {
        pagePath: "pages/index/index",
        iconPath: "assets/icons/home.png",
        selectedIconPath: "assets/icons/home-active.png",
        text: "首页",
      },
      {
        pagePath: "pages/handicraft/handicraft-list",
        iconPath: "assets/icons/cool.png",
        selectedIconPath: "assets/icons/cool-active.png",
        text: "DIY预约",
      },
      {
        pagePath: "pages/product/product-category-list",
        iconPath: "assets/icons/commodity.png",
        selectedIconPath: "assets/icons/commodity-active.png",
        text: "手做の小物",
      },
      {
        pagePath: "pages/cart/cart-list",
        iconPath: "assets/icons/shopping-cart-one.png",
        selectedIconPath: "assets/icons/shopping-cart-one-active.png",
        text: "购物车",
      },
      {
        pagePath: "pages/user/index",
        iconPath: "assets/icons/user.png",
        selectedIconPath: "assets/icons/user-active.png",
        text: "我的",
      },
    ],
  },
  plugins: {
    chooseLocation: {
      version: "1.0.12",
      provider: "wx76a9a06e5b4e693e",
    },
  },
  permission: {
    "scope.userLocation": {
      desc: "你的位置将用于确认收货地址",
    },
  },
  requiredPrivateInfos: ["getLocation", "chooseLocation"],
});
