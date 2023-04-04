import { BaseEntity, TreeModel } from '@/api/model/resultModel';

/**
 * 菜单权限业务对象
 */
export interface SysMenuBo extends BaseEntity {
  /** 菜单ID */
  menuId?: number;
  /** 菜单名称 */
  menuName?: string;
  /** 父菜单ID */
  parentId?: number;
  /** 显示顺序 */
  orderNum?: number;
  /** 路由地址 */
  path?: string;
  /** 组件路径 */
  component?: string;
  /** 路由参数 */
  queryParam?: string;
  /** 是否为外链（0是 1否） */
  isFrame?: number;
  /** 是否缓存（0缓存 1不缓存） */
  isCache?: number;
  /** 菜单类型（M目录 C菜单 F按钮） */
  menuType?: string;
  /** 显示状态（0显示 1隐藏） */
  visible?: string;
  /** 菜单状态（0正常 1停用） */
  status?: string;
  /** 权限标识 */
  perms?: string;
  /** 菜单图标 */
  icon?: string;
  /** 备注 */
  remark?: string;
}
/**
 * 菜单权限视图对象
 */
export interface SysMenuVo {
  /** 菜单ID */
  menuId?: number;
  /** 菜单名称 */
  menuName?: string;
  /** 父菜单ID */
  parentId?: number;
  /** 显示顺序 */
  orderNum?: number;
  /** 路由地址 */
  path?: string;
  /** 组件路径 */
  component?: string;
  /** 路由参数 */
  queryParam?: string;
  /** 是否为外链（0是 1否） */
  isFrame?: number;
  /** 是否缓存（0缓存 1不缓存） */
  isCache?: number;
  /** 菜单类型（M目录 C菜单 F按钮） */
  menuType?: string;
  /** 显示状态（0显示 1隐藏） */
  visible?: string;
  /** 菜单状态（0正常 1停用） */
  status?: string;
  /** 权限标识 */
  perms?: string;
  /** 菜单图标 */
  icon?: string;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 备注 */
  remark?: string;
  /** 子菜单 */
  children?: SysMenuVo[];
}

/**
 * 角色菜单列表树
 */
export interface RoleMenuTreeselect {
  checkedKeys: Array<number>;
  menus: Array<TreeModel<number>>;
}
/**
 * 角色菜单列表树信息
 */
export interface MenuTreeSelectVo {
  /** 选中菜单列表 */
  checkedKeys?: Array<number>;
  /** 菜单下拉树结构列表 */
  menus?: TreeModel<number>[];
}
