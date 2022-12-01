export const imageDataUrl = async (data: number[]): Promise<string> => {
  if (data === undefined) {
    return '';
  }
  const url = await new Promise<string>((r) => {
    const reader = new FileReader();
    reader.onload = () => r(reader.result as string);
    reader.readAsDataURL(new Blob([new Uint8Array(data)]));
  });
  const base64 = url.split(',', 2)[1];
  return base64 ? 'data:image;base64,' + base64 : '';
};
