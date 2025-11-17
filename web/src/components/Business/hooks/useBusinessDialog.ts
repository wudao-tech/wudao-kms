

export const useBusinessDialog = () => {
  const dialogInviteLink = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogInviteLink, {
        visible: true,
        data: row
      })
    }
  })

  const dialogInviteMail = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogInviteMail, {
        visible: true,
        data: row
      })
    }
  })

  const dialogEquipGroup = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipGroup, {
        visible: true,
        data: row
      })
    }
  })
  
  const dialogUploadTree = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogUploadTree, {
        visible: true,
        data: row
      })
    }
  })
  const dialogEquipTypeEdit = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeEdit, {
        visible: true,
        data: row
      })
    }
  })
  const dialogEquipTypeConnList = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeConnList, {
        visible: true,
        data: row
      })
    }
  })
  
  const dialogEquipTypePropEdit = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypePropEdit, {
        visible: true,
        data: row
      })
    }
  })

  const dialogEquipTypeRelease = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeRelease, {
        visible: true,
        data: row
      })
    }
  })
  const dialogEquipTypeRecord = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeRecord, {
        visible: true,
        data: row
      })
    }
  })
  const dialogEquipTypeTsl = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeTsl, {
        visible: true,
        data: row
      })
    }
  })
  const dialogEquipTypeImportJson = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeImportJson, {
        visible: true,
        data: row
      })
    }
  })
  
  const dialogEquipTypeSync = reactive({
    visible: false,
    data: {},
    go(row: {}) {
      Object.assign(dialogEquipTypeSync, {
        visible: true,
        data: row
      })
    }
  })
  
  
  return {
    dialogInviteLink,
    dialogInviteMail,
    dialogEquipGroup,
    dialogUploadTree,
    dialogEquipTypeEdit,
    dialogEquipTypeConnList,
    dialogEquipTypePropEdit,
    dialogEquipTypeRelease,
    dialogEquipTypeRecord,
    dialogEquipTypeTsl,
    dialogEquipTypeImportJson,
    dialogEquipTypeSync,
    
  }
}