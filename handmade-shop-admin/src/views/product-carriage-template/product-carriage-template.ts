import { api } from '@/utils/api-instance'

export const productCarriageTemplateQueryOptions = async (keyword: string, id: string) => {
  return (
    await api.productCarriageTemplateForAdminController.query({
      body: { query: { name: keyword, id } }
    })
  ).content
}
