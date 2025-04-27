export const calculateDurationMinutes = (startTime: string, endTime: string): number => {
    const start = new Date(startTime);
    const end = new Date(endTime);
    
    const diffMs = end.getTime() - start.getTime();
    
    return Math.floor(diffMs / 60000);
  };
  
export  const formatDuration = (minutes: number): string => {
    const days = Math.floor(minutes / (24 * 60));
    const hours = Math.floor((minutes % (24 * 60)) / 60);
    const mins = minutes % 60;
    
    let result = '';
    if (days > 0) result += `${days} day${days !== 1 ? 's' : ''} `;
    if (hours > 0) result += `${hours} hour${hours !== 1 ? 's' : ''} `;
    if (mins > 0 || (days === 0 && hours === 0)) result += `${mins} minute${mins !== 1 ? 's' : ''}`;
    
    return result.trim();
  };