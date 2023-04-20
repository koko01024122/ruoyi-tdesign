import { defineStore } from 'pinia';

import { getInfo, isLogin, login, logout } from '@/api/login';
import { LoginParam, UserInfo } from '@/api/model/loginModel';
import { R } from '@/api/model/resultModel';
import defAva from '@/assets/images/profile.jpg';
import { store } from '@/store';
import { getToken, removeToken, setToken } from '@/utils/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    tenantId: undefined,
    userId: undefined,
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: [],
  }),
  actions: {
    isLogin() {
      return new Promise<boolean>((resolve, reject) => {
        if (this.token) {
          isLogin()
            .then((res) => resolve(res.data))
            .catch((error) => {
              reject(error);
            });
        } else {
          resolve(false);
        }
      });
    },
    login(userInfo: LoginParam) {
      const tenantId = userInfo.tenantId.trim();
      const username = userInfo.username.trim();
      const { password } = userInfo;
      const { code } = userInfo;
      const { uuid } = userInfo;
      return new Promise<void>((resolve, reject) => {
        login(tenantId, username, password, code, uuid)
          .then((res) => {
            setToken(res.data.token);
            this.token = res.data.token;
            this.tenantId = tenantId;
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    getUserInfo() {
      return new Promise<R<UserInfo>>((resolve, reject) => {
        getInfo()
          .then((res) => {
            const { user } = res.data;
            const avatar = !user.avatar ? defAva : user.avatar;

            if (res.data.roles && res.data.roles.length > 0) {
              // 验证返回的roles是否是一个非空数组
              this.roles = res.data.roles;
              this.permissions = res.data.permissions;
            } else {
              this.roles = ['ROLE_DEFAULT'];
            }
            this.userId = user.userId;
            this.name = user.userName;
            this.avatar = avatar;
            resolve(res);
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    logout() {
      return new Promise<void>((resolve, reject) => {
        logout()
          .then(() => {
            this.token = '';
            this.tenantId = undefined;
            this.userId = undefined;
            this.roles = [];
            this.permissions = [];
            removeToken();
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
  },
  // persist: {
  //   afterRestore: (ctx) => {
  //     if (ctx.store.roles && ctx.store.roles.length > 0) {
  //       const permissionStore = usePermissionStore();
  //       // @ts-ignore
  //       permissionStore.initRoutes(ctx.store.roles);
  //     }
  //   },
  // },
});

export function getUserStore() {
  return useUserStore(store);
}
