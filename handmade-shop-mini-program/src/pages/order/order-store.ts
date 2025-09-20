import { api } from "@/utils/api-instance";
import Taro from "@tarojs/taro";

export const handleProductWeChatPay = (id: string) => {
  return new Promise<boolean>((resolve, reject) => {
    api.productOrderForFrontController
      .prepayWechat({ id })
      .then((res) => {
        Taro.requestPayment({
          nonceStr: res.nonceStr,
          package: res.packageValue,
          paySign: res.paySign,
          timeStamp: res.timeStamp,
          signType: res.signType as "RSA",
          success: () => {
            Taro.showToast({
              title: "支付成功",
              icon: "success",
            });
            resolve(true);
          },
          fail: (res) => {
            console.log(res);
            reject(res);
            Taro.hideLoading();
          },
        });
      })
      .catch((err) => {
        reject(err);
      });
  });
};
export const handleHandicraftWeChatPay = (id: string) => {
  return new Promise<boolean>((resolve, reject) => {
    api.handicraftOrderForFrontController
      .prepayWechat({ id })
      .then((res) => {
        Taro.requestPayment({
          nonceStr: res.nonceStr,
          package: res.packageValue,
          paySign: res.paySign,
          timeStamp: res.timeStamp,
          signType: res.signType as "RSA",
          success: () => {
            Taro.showToast({
              title: "支付成功",
              icon: "success",
            });
            resolve(true);
          },
          fail: (res) => {
            console.log(res);
            reject(res);
            Taro.hideLoading();
          },
        });
      })
      .catch((err) => {
        reject(err);
      });
  });
};
