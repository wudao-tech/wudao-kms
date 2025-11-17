import useUserStore from '@/store/modules/user';

/**
 * 字符权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export const checkPermi = (value: any) => {
  if (value && value instanceof Array && value.length > 0) {
    const permissions = useUserStore().permissions;
    const permissionDatas = value;
    const all_permission = '*:*:*';

    const hasPermission = permissions.some((permission) => {
      return all_permission === permission || permissionDatas.includes(permission);
    });

    if (!hasPermission) {
      return false;
    }
    return true;
  } else {
    console.error(`need roles! Like checkPermi="['system:user:add','system:user:edit']"`);
    return false;
  }
};

/**
 * 角色权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export const checkRole = (value: any): boolean => {
  if (value && value instanceof Array && value.length > 0) {
    const roles = useUserStore().roles;
    const permissionRoles = value;
    const super_admin = 'admin';

    const hasRole = roles.some((role) => {
      return super_admin === role || permissionRoles.includes(role);
    });

    if (!hasRole) {
      return false;
    }
    return true;
  } else {
    console.error(`need roles! Like checkRole="['admin','editor']"`);
    return false;
  }
};

export const routeInit = (routes: any) => {
  return routeFormat(routes, '')
}

const routeFormat = (routes: any, basePath: any) => {
  return routes.map((route: any) => {
    let { path, children } = route
    if (path[0] !== '/') {
      path = basePath + '/' + path
    }
    if (children && children.length) {
      children = routeFormat(children, path)
    }

    return Object.assign(route, {
      path,
      children
    })
  })
}

export const exportRouteIndex = (routes: any[]) => {
  const m: any = new Map()
  exportRouteIndexFn(routes, m, null)
  return m
}
const exportRouteIndexFn = (routes: any[], rootM: any, parentIndex: number[] | null) => {
  routes.forEach((t, i) => {
    parentIndex = parentIndex || []
    rootM.set(t.path, [...parentIndex, i])
    if (t.children && t.children.length) {
      exportRouteIndexFn(t.children, rootM, rootM.get(t.path))
    }
  })
}

export const findFirstChildPath = (routeItem: any) => {
  if (routeItem.children && routeItem.children.length) {
    return findFirstChildPath(routeItem.children[0])
  }
  return routeItem.path
}
