import { api } from '@/utils/api-instance'

export const handicraftCategoryQueryOptions = async (keyword: string, id: string) => {
  return (
    await api.handicraftCategoryForAdminController.query({ body: { query: { name: keyword, id } } })
  ).content
}
