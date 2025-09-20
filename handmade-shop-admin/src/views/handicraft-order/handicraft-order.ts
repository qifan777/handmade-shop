import { api } from '@/utils/api-instance'

export const handicraftOrderQueryOptions = async (keyword: string, id: string) => {
  return (
    await api.handicraftOrderForAdminController.query({ body: { query: { name: keyword, id } } })
  ).content
}
