import { getDicts } from '@/api/system/dict/data';
import { useDictStore } from '@/store/modules/dict';
/**
 * 获取字典数据
 */
export const useDict = (...args: string[]): { [key: string]: DictDataOption[] } => {
  const res = ref<{
    [key: string]: DictDataOption[];
  }>({});
  return (() => {
    args.forEach(async (dictType) => {
      res.value[dictType] = [];
      const dicts = useDictStore().getDict(dictType);
      if (dicts) {
        res.value[dictType] = dicts;
      } else {
        await getDicts(dictType).then((resp) => {
          res.value[dictType] = resp.data.reduce((acc, n) => {
            let { label: label, value: value, listClass: elTagType, cssClass: elTagClass, remark: remark } = n
            if (!acc.find(t => t.value === value)) {
              let values = null
              if (value === 'true') {
                values = true
              } else if (value === 'false') {
                values = false
              } else {
                values = value
              }
              acc.push({label, value: values, elTagType, elTagClass, remark})
            }
            return acc
          }, [])
          // res.value[dictType] = resp.data.map(
          //   (p): DictDataOption => ({ label: p.label, value: p.value, elTagType: p.listClass, elTagClass: p.cssClass })
          // );
          useDictStore().setDict(dictType, res.value[dictType]);
        });
      }
    });
    return res.value;
  })();
};
