export function formatDate(date?: number) {
  return date ? new Date(date).toLocaleString() : undefined
}

export function round(value: number) {
  return Math.round(value * 100) / 100
}

export function formatDuration(duration: number) {
  if(duration <= 0) return "00:00:00"
  return new Date(duration).toISOString().slice(11, 19)
}

export function formatNumber(number: number) {
  return new Intl.NumberFormat().format(number)
}

export function isEmpty(value: any) {
  if(Array.isArray(value)) return value.length === 0

  if(typeof value === "object" && value !== null)
    return Object.keys(value).length == 0

  return false;
}