import { View, Text } from "react-native";

export default function MoreScreen() {
  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-lg font-bold">더보기</Text>
      <Text className="mt-2 text-gray-500">프로필 및 설정</Text>
    </View>
  );
}
